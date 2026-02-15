# CLAUDE.md

Claude Code (claude.ai/code)가 이 저장소의 코드를 작업할 때 참고할 지침입니다.

## 프로젝트 개요

**SPRINGBOOT-BE-DEV-STARTER** - 다음 기능을 포함한 프로덕션 준비 Spring Boot RESTful API 스타터 키트:
- JWT 기반 상태 비보존 인증
- Flyway 마이그레이션이 포함된 PostgreSQL
- Docker 컨테이너화
- TDD 기반 개발 예제 (User, Auth, Todo 도메인)

## 아키텍처

### 상위 설계
프로젝트는 **포트 & 어댑터 패턴(헥사고날 아키텍처)**을 도메인 주도 설계와 함께 사용합니다:

```
HTTP Request (JSON)
       ↓
┌─ 입력 어댑터 (Inbound) ─────────────────┐
│                                          │
│  Request DTO                             │
│       ↓ (Request → Command 변환)         │
│  REST Controller                         │  SecurityFilter (JWT)
│       ↓                                  │       ↓
│  UseCase Port (Input) ← Command          │  Authentication
│       ↓                                  │
├──────────────────────────────────────────┤
│   도메인 계층 (Hexagon)                 │
│                                          │
│  ApplicationService                      │
│  (UseCase 구현)                          │
│  Command 처리 → Result 생성              │
│       ↓                                  │
│  Out Port (영속성)                       │
│       ↓                                  │
├──────────────────────────────────────────┤
│  출력 어댑터 (Outbound)                  │
│                                          │
│  JPA Adapter                             │
│  (Port 구현) → Entity 변환               │
│       ↓                                  │
│  PostgreSQL Database                    │
│                                          │
└──────────────────────────────────────────┘
       ↑
   (Result ← Entity)
       ↓
Response DTO (JSON)
       ↓
HTTP Response
```

**데이터 변환 흐름**: `Request` → `Command` → `UseCase` → `Result` → `Response`

**핵심 원칙**:
- 도메인이 중심이며, 어댑터가 도메인에 의존 (의존성 역전)
- 각 계층은 다음 계층의 구현 세부사항을 알지 못함 (정보 은닉)
- 도메인 간 통신은 Out Port를 통해서만 수행

### 도메인 구조 (헥사고날 아키텍처)
각 도메인(예: `domain/user/`, `domain/todo/`)은 포트 & 어댑터 패턴을 따르며 다음 계층으로 구성됩니다:

#### **Domain 계층** (도메인 중심)
- **Entity**: `@Entity` JPA 엔티티, 감사 필드(createdAt, updatedAt)를 위해 `BaseEntity` 확장
- **Enums**: 상태 열거형(예: `TodoStatus`)
- **Position**: `domain/` 폴더 (예: `domain/user/domain/User.java`)

#### **Application 계층** (사용 사례 정의)
- **Port (Input)**: UseCase 인터페이스
  - `CreateXxxUseCase`, `GetXxxUseCase`, `UpdateXxxUseCase`, `DeleteXxxUseCase` 등
  - 위치: `application/port/in/`

- **Port (Output)**: 저장소 포트 인터페이스 (예: `UserPort`, `TodoPort`)
  - 기존 Repository를 대체하는 추상화
  - 위치: `application/port/out/`

- **Service**: `@Service` ApplicationService (UseCase 구현체)
  - 모든 UseCase 인터페이스 구현
  - Port 의존 (Repository 아님)
  - `@Transactional` 포함, 비즈니스 로직 포함
  - 위치: `application/service/`

#### **Adapter 계층** (기술 구현 분리)
- **Input Adapter (in/web/)**
  - `@RestController`: REST 엔드포인트
  - UseCase 인터페이스 주입받음
  - SecurityContext에서 `Authentication.getName()` 사용
  - **데이터 흐름**: `Request` → `Command` → `UseCase` → `Result` → `Response`
  - **DTOs & Commands**:
    - `adapter/in/web/dto/`: 요청/응답 클래스 (HTTP 계층)
    - `application/port/in/command/`: Command 클래스 (UseCase 입력)
    - `application/port/in/command/`: Result 클래스 (UseCase 출력)

- **Output Adapter (out/persistence/)**
  - `Spring Data JPA`: JPA 저장소 (`UserJpaRepository`, `TodoJpaRepository`)
  - `Port 구현체`: Adapter (`UserPersistenceAdapter`, `TodoPersistenceAdapter`)
  - Out Port 인터페이스 구현

**디렉토리 구조 예시**:
```
domain/user/
├── domain/
│   └── User.java                          # JPA Entity
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateUserUseCase.java     # UseCase 인터페이스
│   │   │   ├── GetUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   └── DeleteUserUseCase.java
│   │   └── out/
│   │       └── UserPort.java              # Out Port (저장소 추상화)
│   ├── service/
│   │   └── UserService.java               # ApplicationService (모든 UseCase 구현)
│   └── port/in/command/                   # Command & Result 클래스
│       ├── CreateUserCommand.java
│       ├── UpdateUserCommand.java
│       └── UserResult.java
└── adapter/
    ├── in/web/
    │   ├── UserController.java            # REST Controller
    │   └── dto/
    │       ├── UserRequest.java           # HTTP Request DTO
    │       └── UserResponse.java          # HTTP Response DTO
    └── out/persistence/
        ├── UserJpaRepository.java         # Spring Data JPA
        └── UserPersistenceAdapter.java    # Port 구현체
```

**핵심 패턴**:
- 서비스는 `UserPort` 출력 포트에 의존 (저장소가 아님)
- 컨트롤러는 UseCase 입력 포트를 주입받음
- `Authentication.getName()`으로 JWT 토큰에서 `userId` 추출

### 공통 패턴

**포트 & 어댑터 (헥사고날) 패턴**:
- **입력 포트 (In Port)**: UseCase 인터페이스로 외부(HTTP, gRPC 등)의 요청을 정의
- **출력 포트 (Out Port)**: 저장소, 외부 API 등의 의존성을 추상화
- **입력 어댑터 (In Adapter)**: Controller가 구현하여 HTTP 요청을 UseCase로 변환
- **출력 어댑터 (Out Adapter)**: JPA Repository, REST Client 등이 포트를 구현하여 기술 세부사항 처리
- **이점**: 느슨한 결합, 높은 응집도, 우수한 테스트 가능성, 기술 변경에 유연함

**의존성 흐름** (의존성 역전):
```
Controller (Adapter) ─→ UseCase Port (Interface)
                            ↑
                            │ implements
                            │
Service (Application) ──→ Out Port (Interface)
                            ↑
                            │ implements
                            │
JPA Adapter (Adapter) → Database
```
→ 도메인은 어댑터를 의존하지 않으며, 어댑터가 도메인을 의존함

**예외 처리**:
- 모든 예외는 `GlobalExceptionHandler`를 통해 처리됨
- 도메인 오류의 경우 `BusinessException(ErrorCode, message)` 사용
- `ErrorCode` 열거형(`common/exception/`에 위치)은 표준화된 오류 코드와 HTTP 상태 정의

**응답 형식**:
- 모든 API 응답은 `ApiResponse<T>`로 래핑됨(`common/response/`에 위치)
- 성공: `ApiResponse.success(data, message)`
- 실패: `ApiResponse.fail(message)`

**인증 흐름**:
1. 클라이언트가 `POST /api/v1/auth/login`으로 로그인
2. `accessToken` (1시간)과 `refreshToken` (7일) 수신
3. `JwtAuthFilter`가 각 요청 시 토큰 검증, `SecurityContext` 채움
4. 컨트롤러가 현재 사용자 ID 접근: `Long userId = Long.parseLong(authentication.getName())`

**데이터베이스 마이그레이션**:
- Flyway가 `src/main/resources/db/migration/`에서 자동으로 마이그레이션 실행
- 형식: `V{N}__{description}.sql` (예: `V1__init_schema.sql`, `V2__create_todos_table.sql`)
- 기본 타임스탐프를 위해 `CURRENT_TIMESTAMP` 사용

## 빌드 및 개발 명령어

### 빌드
```bash
./gradlew build          # 테스트 포함 전체 빌드
./gradlew clean build    # 깨끗한 재빌드
./gradlew assemble       # 테스트 없이 빌드
./gradlew bootJar        # 실행 가능한 JAR 빌드
```

### 테스트 실행
```bash
./gradlew test                                    # 모든 테스트
./gradlew test --tests TodoServiceTest            # 단일 테스트 클래스
./gradlew test --tests TodoServiceTest.testCreateTodo_Success  # 단일 테스트 메서드
```

### 로컬 개발 (Docker 없이)
```bash
# localhost:5432에서 실행 중인 PostgreSQL 필요
./gradlew bootRun --args='--spring.profiles.active=local'
```

### Docker 개발
```bash
docker-compose up -d      # PostgreSQL + 앱 컨테이너 시작
docker-compose down       # 컨테이너 중지 및 제거
docker-compose logs app   # 앱 로그 보기
docker logs starter_app   # 실행 중인 앱 컨테이너 로그 보기
```

### Gradle Wrapper 업데이트
```bash
./gradlew wrapper --gradle-version 8.4
```

## 주요 설정 파일

### `application.yml`
- **환경 변수**: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, `JWT_SECRET`, `SERVER_PORT`
- **JWT 설정**: `jwt.secret`, `jwt.access-expiration` (3600000ms = 1시간), `jwt.refresh-expiration` (604800000ms = 7일)
- **Flyway**: `spring.flyway.locations: classpath:db/migration` (`ddl-auto: validate`일 때 시작 시 자동 실행)
- **보안**: CSRF 비활성화, 상태 비보존 세션 (JWT), CORS 기본적으로 미구성

### `.env` 파일
Docker 개발에 필수. `.env.example`에서 복사하고 커스터마이징:
```
DB_HOST=postgres           # Docker에서는 서비스명 "postgres" 사용
DB_HOST=localhost          # Docker 없는 로컬 개발용
```

### `build.gradle`
- **주요 의존성**: Spring Boot 3.3.0, Spring Security 6.x, JJWT 0.12.3, Flyway 10.4.1, PostgreSQL 42.7.2
- **테스트**: JUnit 5, Mockito, Spring Security Test, Testcontainers (PostgreSQL 통합 테스트용)
- **코드 생성**: Lombok, MapStruct 애노테이션 프로세서

## 중요한 구현 노트

### JWT 토큰 제공자 (Security/JwtTokenProvider.java)
- **JJWT 0.12.3 API** 사용: `Jwts.parser().verifyWith(key).build().parseSignedClaims(token)`
- 이전 버전 (parserBuilder)은 호환되지 않음
- HS256용 `SecretKeySpec`으로 생성된 `SecretKey`

### JPA 감사 (Common/Entity/BaseEntity.java)
- `@EntityListeners(AuditingEntityListener.class)`를 포함한 `@MappedSuperclass`
- 필드: `@CreatedDate LocalDateTime createdAt`, `@LastModifiedDate LocalDateTime updatedAt`
- Spring Data에 의해 자동으로 채워짐, **빌더에서 수동으로 설정하지 않기**
- `JpaAuditConfig`를 통해 활성화 (한 곳에만; 중복 `@EnableJpaAuditing` 피하기)

### 입력 어댑터 (Controller)에서의 사용자 인증
- Spring Security는 `Authentication.getName()`에 사용자 ID 문자열 채움
- 패턴: `Long userId = Long.parseLong(authentication.getName())`
- 어댑터에서 userId를 추출하여 UseCase에 전달
- 서비스 계층은 userId를 매개변수로 받아 행 수준 권한 부여 처리

```java
@RestController
@RequestMapping("/api/v1/todos")
public class TodoController {
  private final CreateTodoUseCase createTodoUseCase;

  @PostMapping
  public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
      @RequestBody TodoRequest request,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    // UseCase에 userId 전달
    TodoResponse response = createTodoUseCase.createTodo(userId, request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success(response));
  }
}
```

### 출력 포트 (Out Port) 설계
- Out Port는 저장소 인터페이스를 추상화
- 기술 독립적이어야 함 (JPA 구현 세부사항 숨김)
- 예: `UserPort`, `TodoPort`

```java
public interface UserPort {
  User save(User user);
  Optional<User> findById(Long id);
  Optional<User> findByEmail(String email);
  Optional<User> findByUsername(String username);
  boolean existsByEmail(String email);
  boolean existsByUsername(String username);
  Page<User> findAll(Pageable pageable);
  void delete(User user);
}
```

### 출력 어댑터 (PersistenceAdapter) 구현
- Out Port를 구현하여 JPA Repository 위임
- JPA 구현 세부사항을 숨김
- 서비스는 Port에만 의존

```java
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {
  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }
  // ... 다른 메서드들
}
```

### 새로운 도메인 추가 (헥사고날 아키텍처)

1. **디렉토리 구조 생성**
   ```bash
   src/main/java/com/example/starter/domain/{domain}/
   ├── domain/                      # 도메인 엔티티
   ├── application/
   │   ├── port/
   │   │   ├── in/                 # UseCase 인터페이스
   │   │   └── out/                # Out Port 인터페이스
   │   └── service/                # ApplicationService
   └── adapter/
       ├── in/web/                 # REST Controller
       │   └── dto/
       └── out/persistence/        # JPA Adapter
   ```

2. **도메인 계층 (domain/)**
   - Entity 클래스 작성 (BaseEntity 확장)
   - Enum 작성 (필요시)

3. **애플리케이션 계층 (application/)**
   - **입력 포트 (port/in/)**: UseCase 인터페이스 정의
     - `CreateXxxUseCase`, `GetXxxUseCase`, `UpdateXxxUseCase`, `DeleteXxxUseCase` 등
   - **출력 포트 (port/out/)**: `XxxPort` 인터페이스 정의
     - 저장소 메서드 추상화 (save, findById, findAll, delete 등)
   - **서비스 (service/)**: ApplicationService 구현
     - 모든 UseCase 인터페이스 구현
     - Out Port 의존 (주입받음)
     - `@Transactional` 포함

4. **어댑터 계층 (adapter/)**
   - **입력 어댑터 (in/web/)**:
     - `XxxController.java`: UseCase 인터페이스 주입
     - `dto/XxxRequest.java`, `XxxResponse.java`: 요청/응답
   - **출력 어댑터 (out/persistence/)**:
     - `XxxJpaRepository.java`: Spring Data JPA 저장소
     - `XxxPersistenceAdapter.java`: Out Port 구현

5. **Flyway 마이그레이션**
   ```bash
   src/main/resources/db/migration/V{N}__{domain}.sql
   ```

6. **테스트 추가**
   ```bash
   src/test/java/com/example/starter/domain/{domain}/
   ├── application/service/
   │   └── XxxServiceTest.java     # Out Port Mock
   └── adapter/in/web/
       └── XxxControllerTest.java  # UseCase Mock
   ```

7. **다중 테넌트 안전성**
   - 서비스 메서드는 `userId` 매개변수 수용
   - 데이터 조회 시 사용자 소유권 확인 필수
   - 예: `findByIdAndUserId(id, userId)` 패턴 사용

### Lombok 주의사항
- 기본값이 있는 필드의 경우 `@Builder.Default` 필수 (예: `@Default private String status = "PENDING"`)
- 빌더를 통해 엔티티를 업데이트할 때 감사 필드(createdAt/updatedAt) 제외하여 `save()` 시 원본 보존

## Swagger/OpenAPI 접근

- **UI**: http://localhost:8080/swagger-ui.html
- **JSON**: http://localhost:8080/v3/api-docs
- `config/SwaggerConfig.java`에서 JWT Bearer 인증 버튼으로 구성됨
- 모든 컨트롤러는 `@SecurityRequirement(name = "Bearer Authentication")`를 사용하여 잠금 아이콘 표시

## 공통 개발 워크플로우 (헥사고날 아키텍처)

### TDD 기반 개발 흐름

1. **포트 정의** (계약 먼저)
   - UseCase 입력 포트 인터페이스 작성: `application/port/in/XxxUseCase.java`
   - Out Port 출력 포트 인터페이스 작성: `application/port/out/XxxPort.java`

2. **테스트 작성** (Red)
   - `src/test/java/domain/{domain}/application/service/{Entity}ServiceTest.java`
   - Out Port를 Mock으로 주입
   - UseCase 메서드 테스트

3. **서비스 구현** (Green)
   - `src/main/java/domain/{domain}/application/service/{Entity}Service.java`
   - 모든 UseCase 인터페이스 구현
   - Out Port 메서드 호출

4. **출력 어댑터 구현**
   - `application/port/out/{Entity}Port.java` 인터페이스 작성
   - `adapter/out/persistence/{Entity}JpaRepository.java` (Spring Data JPA)
   - `adapter/out/persistence/{Entity}PersistenceAdapter.java` (Port 구현)

5. **입력 어댑터 구현**
   - `adapter/in/web/{Entity}Controller.java`: UseCase 주입, 라우팅
   - `adapter/in/web/dto/{Entity}Request.java`, `{Entity}Response.java`

6. **컨트롤러 테스트**
   - `src/test/java/domain/{domain}/adapter/in/web/{Entity}ControllerTest.java`
   - UseCase를 Mock으로 주입

7. **도메인 엔티티 및 마이그레이션**
   - `domain/{Entity}.java`: JPA Entity 작성
   - `db/migration/V{N}__{domain}.sql`: Flyway 마이그레이션

8. **테스트 실행 및 검증**
   ```bash
   # 모든 테스트
   ./gradlew test

   # 특정 도메인 테스트
   ./gradlew test --tests "{domain}ServiceTest"
   ```

9. **로컬 테스트**
   ```bash
   docker-compose up -d    # PostgreSQL 시작
   ./gradlew bootRun       # 애플리케이션 실행
   ```
   - Swagger UI 방문: http://localhost:8080/swagger-ui.html

10. **Git 커밋** (한국어 메시지)
    ```bash
    git add .
    git commit -m "feat: {도메인} 기능 추가 - {설명}"
    ```

## 디버깅 팁

- **포트 8080 이미 사용 중**: `docker-compose down` 또는 `.env`에서 `SERVER_PORT` 변경
- **Flyway 마이그레이션 실패**: 마이그레이션 파일의 SQL 구문 확인; 스키마가 `application.yml`과 일치하는지 확인
- **JWT 토큰 유효하지 않음**: `JWT_SECRET`이 ≥32자이고 `.env`와 일치하는지 확인
- **데이터베이스 연결 실패**: PostgreSQL이 실행 중인지 확인; `.env`에서 `DB_HOST`, `DB_PORT`, 자격증명 확인
- **Lombok 작동하지 않음**: IDE에 Lombok 플러그인이 설치되어 있는지 확인 (IntelliJ: Settings → Plugins → Lombok)

## 테스트 전략 (헥사고날 아키텍처)

### 단위 테스트 (Unit Test)
- **위치**: `src/test/java/domain/{domain}/application/service/{Entity}ServiceTest.java`
- **방식**: Out Port를 Mock으로 주입하여 서비스 로직만 테스트
- **도구**: JUnit 5 + Mockito
- **예제**:
  ```java
  @ExtendWith(MockitoExtension.class)
  class UserServiceTest {
    @Mock
    private UserPort userPort;  // Out Port Mock

    @InjectMocks
    private UserService userService;

    @Test
    void testCreateUser_Success() {
      // Given: Mock 설정
      when(userPort.existsByEmail(...)).thenReturn(false);
      when(userPort.save(...)).thenReturn(testUser);

      // When: UseCase 호출
      UserResponse response = userService.createUser(request);

      // Then: 검증
      assertNotNull(response);
      verify(userPort, times(1)).save(any(User.class));
    }
  }
  ```

### 통합 테스트 (Integration Test)
- **위치**: `src/test/java/domain/{domain}/adapter/in/web/{Entity}ControllerTest.java`
- **방식**: MockMvc + UseCase Mock을 사용한 엔드포인트 테스트
- **도구**: `@SpringBootTest` + `@AutoConfigureMockMvc`
- **예제**:
  ```java
  @SpringBootTest
  @AutoConfigureMockMvc
  class UserControllerTest {
    @MockBean
    private CreateUserUseCase createUserUseCase;  // UseCase Mock

    @Test
    void testCreateUser_Success() throws Exception {
      when(createUserUseCase.createUser(...))
        .thenReturn(testUserResponse);

      mockMvc.perform(post("/api/v1/users")
          .contentType(MediaType.APPLICATION_JSON)
          .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated());
    }
  }
  ```

### E2E 테스트 (선택적)
- **Testcontainers**: PostgreSQL 컨테이너를 사용한 완전 통합 테스트
- **의존성**: `build.gradle`에 포함됨
- **실행**: 실제 DB와 모든 계층 테스트

### 테스트 커버리지 가이드
- **서비스 계층**: 90% 이상 (비즈니스 로직)
- **어댑터 계층**: 70% 이상 (변환 로직)
- **도메인 계층**: 필요시 (entity 비즈니스 메서드)

### 실행 명령어
```bash
# 모든 테스트
./gradlew test

# 특정 도메인
./gradlew test --tests "com.example.starter.domain.user.application.service.UserServiceTest"
./gradlew test --tests "com.example.starter.domain.todo.application.service.TodoServiceTest"

# 특정 테스트 메서드
./gradlew test --tests "UserServiceTest.testCreateUser_Success"

# 테스트 리포트
./gradlew test --info
```

### 예제 참조
- `src/test/java/domain/user/application/service/UserServiceTest.java`
- `src/test/java/domain/user/adapter/in/web/UserControllerTest.java`
- `src/test/java/domain/todo/application/service/TodoServiceTest.java`
- `src/test/java/domain/todo/adapter/in/web/TodoControllerTest.java`

## 성능 및 보안 고려사항

- **트랜잭션**: 서비스 계층의 모든 쓰기 작업은 `@Transactional` 사용
- **읽기 전용**: 쿼리 메서드는 `@Transactional(readOnly = true)` 사용
- **암호 인코딩**: 사용자 암호에 `BCryptPasswordEncoder` 사용; 평문 저장 금지
- **토큰 만료**: 보안을 위한 접근 토큰 (1시간); 로그인 빈도 감소를 위한 새로고침 토큰 (7일)
- **CORS**: 기본적으로 비활성화; 프론트엔드가 필요한 경우 `CorsConfigurationSource` 빈 추가
- **SQL 인젝션**: JPA 매개변수화 쿼리는 인젝션 방지; 마이그레이션의 커스텀 SQL은 매개변수화 쿼리 사용해야 함

## 헥사고날 아키텍처 가이드 (2026-02-14 전환 완료)

### 왜 헥사고날 아키텍처인가?
- **느슨한 결합**: 도메인이 어댑터를 의존하지 않음
- **높은 응집도**: 관련 로직이 한 곳에 모임
- **테스트 용이성**: Port를 Mock으로 쉽게 대체 가능
- **확장성**: 새로운 기술 도입 시 어댑터만 변경
- **명확한 책임**: 각 계층의 역할이 명확함

### 실전 예시: User 도메인 전환 후 구조
```
현재 프로젝트 User 도메인 구조:
domain/user/
├── domain/
│   └── User.java                          # JPA Entity (도메인 모델)
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── CreateUserUseCase.java
│   │   │   ├── GetUserUseCase.java
│   │   │   ├── UpdateUserUseCase.java
│   │   │   └── DeleteUserUseCase.java
│   │   └── out/
│   │       └── UserPort.java             # 저장소 포트 (저장소 아님!)
│   └── service/
│       └── UserService.java              # ApplicationService (모든 UseCase 구현)
└── adapter/
    ├── in/web/
    │   ├── UserController.java           # REST Adapter
    │   └── dto/
    │       ├── UserRequest.java
    │       └── UserResponse.java
    └── out/persistence/
        ├── UserJpaRepository.java        # Spring Data JPA
        └── UserPersistenceAdapter.java   # Port 구현체 (JPA 위임)

테스트 구조:
test/java/domain/user/
├── application/service/
│   └── UserServiceTest.java              # Out Port 모킹
└── adapter/in/web/
    └── UserControllerTest.java           # UseCase 모킹
```

### 주요 변경사항 (레이어드 → 헥사고날)
| 항목 | Before | After |
|------|--------|-------|
| **Repository** | 서비스가 직접 의존 | Port 인터페이스로 추상화 |
| **Service** | `@Service` + 비즈니스 로직 | UseCase 구현 + 트랜잭션 |
| **Controller** | Service 직접 호출 | UseCase 인터페이스 호출 |
| **테스트** | Repository Mock | Port Mock + UseCase Mock |
| **기술 변경** | 서비스 수정 필요 | Adapter만 수정 |

### 실수하기 쉬운 것들
⚠️ **피해야 할 패턴**:
```java
// ❌ 잘못된 예: Repository를 서비스에 직접 주입
@Service
public class UserService {
  private final UserRepository userRepository;  // ❌ Port 아님!
}

// ❌ 잘못된 예: 어댑터에서 다른 어댑터 호출
@RestController
public class UserController {
  private final UserService userService;  // ❌ 어댑터끼리 호출
}

// ❌ 잘못된 예: Out Port가 JPA 구현을 노출
public interface UserPort extends JpaRepository<User, Long> {  // ❌
}
```

✅ **올바른 패턴**:
```java
// ✅ 올바른 예: Out Port 의존
@Service
public class UserService implements CreateUserUseCase {
  private final UserPort userPort;  // Port 인터페이스
}

// ✅ 올바른 예: UseCase 인터페이스 호출
@RestController
public class UserController {
  private final CreateUserUseCase createUserUseCase;  // UseCase 인터페이스
}

// ✅ 올바른 예: 기술 독립적인 Out Port
public interface UserPort {
  User save(User user);
  Optional<User> findById(Long id);
}

// ✅ 올바른 예: Adapter가 Port 구현
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {
  private final UserJpaRepository userJpaRepository;
}
```

### 의존성 다이어그램 검증
모든 의존성이 **안쪽으로만** 향해야 함:
```
Controller (Adapter)
    ↓ 의존
UseCase Port (Interface) ← Service (Application)
    ↑ 의존
Out Port (Interface) ← Adapter (출력)
    ↑ 의존
Database
```

### 문제 해결: 컴파일 오류 시
1. **Import 순환 참조 확인**:
   - application 계층이 adapter를 import하면 안 됨
   - adapter가 다른 adapter를 import하면 안 됨

2. **Port 메서드 누락 확인**:
   - Service가 사용하는 메서드가 Out Port에 정의되어 있는지 확인

3. **UseCase 구현 확인**:
   - Service가 모든 관련 UseCase를 implements하는지 확인

## 배포 노트

- Flyway는 시작 시 자동으로 실행; 마이그레이션이 멱등성인지 확인
- Docker 이미지는 **다중 단계**로 빌드됨: Gradle 빌드 단계 → JRE 런타임 단계 (작은 이미지)
- 헬스 체크 엔드포인트: `GET /actuator/health` (Docker 헬스 프로브에서 구성됨)
- 환경 변수(예: `DB_HOST=prod-server`)나 Java 시스템 속성을 통해 `application.yml` 속성 재정의

---

**마지막 업데이트**: 2026-02-14 (헥사고날 아키텍처 전환 완료)
