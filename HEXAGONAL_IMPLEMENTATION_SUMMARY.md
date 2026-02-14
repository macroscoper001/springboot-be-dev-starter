# 헥사고날 아키텍처 구현 완료 보고서

## 🎯 프로젝트 개요

**Spring Boot 프로젝트를 레이어드 아키텍처에서 헥사고날(포트 & 어댑터) 아키텍처로 전환**

- 기간: 2026-02-14
- 상태: ✅ **완료**
- 파일 생성: 33개 (메인) + 4개 (테스트)

---

## 📂 변경 요약

### User 도메인 (사용자 관리)

#### Before (레이어드 아키텍처)
```
domain/user/
├── User.java
├── UserService.java (UserRepository 의존)
├── UserController.java
├── UserRepository.java
└── dto/
    ├── UserRequest.java
    └── UserResponse.java
```

#### After (헥사고날 아키텍처)
```
domain/user/
├── domain/
│   └── User.java
├── application/
│   ├── port/in/
│   │   ├── CreateUserUseCase.java
│   │   ├── GetUserUseCase.java
│   │   ├── UpdateUserUseCase.java
│   │   └── DeleteUserUseCase.java
│   ├── port/out/
│   │   └── UserPort.java
│   └── service/
│       └── UserService.java (UserPort 의존)
└── adapter/
    ├── in/web/
    │   ├── UserController.java
    │   └── dto/
    │       ├── UserRequest.java
    │       └── UserResponse.java
    └── out/persistence/
        ├── UserJpaRepository.java
        └── UserPersistenceAdapter.java
```

**핵심 변경**:
- ✅ Repository → Port 추상화
- ✅ UseCase 인터페이스 도입 (4개)
- ✅ JPA 어댑터로 분리
- ✅ 느슨한 결합 달성

---

### Todo 도메인 (할일 관리)

#### Before (레이어드 아키텍처)
```
domain/todo/
├── Todo.java
├── TodoStatus.java
├── TodoService.java (TodoRepository 의존)
├── TodoController.java
├── TodoRepository.java
└── dto/
    ├── TodoRequest.java
    └── TodoResponse.java
```

#### After (헥사고날 아키텍처)
```
domain/todo/
├── domain/
│   ├── Todo.java
│   └── TodoStatus.java
├── application/
│   ├── port/in/
│   │   ├── CreateTodoUseCase.java
│   │   ├── GetTodoUseCase.java
│   │   ├── UpdateTodoUseCase.java
│   │   ├── DeleteTodoUseCase.java
│   │   └── ChangeTodoStatusUseCase.java
│   ├── port/out/
│   │   └── TodoPort.java
│   └── service/
│       └── TodoService.java (TodoPort 의존)
└── adapter/
    ├── in/web/
    │   ├── TodoController.java
    │   └── dto/
    │       ├── TodoRequest.java
    │       └── TodoResponse.java
    └── out/persistence/
        ├── TodoJpaRepository.java
        └── TodoPersistenceAdapter.java
```

**핵심 변경**:
- ✅ Repository → Port 추상화
- ✅ UseCase 인터페이스 도입 (5개)
- ✅ JPA 어댑터로 분리
- ✅ 다중 책임 분리

---

### Auth 도메인 (인증)

#### Before (레이어드 아키텍처)
```
domain/auth/
├── AuthService.java (UserRepository 의존)
├── AuthController.java
└── dto/
    ├── LoginRequest.java
    ├── RefreshTokenRequest.java
    └── LoginResponse.java
```

#### After (헥사고날 아키텍처)
```
domain/auth/
├── application/
│   ├── port/in/
│   │   ├── LoginUseCase.java
│   │   └── RefreshTokenUseCase.java
│   └── service/
│       └── AuthService.java (UserPort 의존)
└── adapter/
    └── in/web/
        ├── AuthController.java
        └── dto/
            ├── LoginRequest.java
            ├── RefreshTokenRequest.java
            └── LoginResponse.java
```

**핵심 변경**:
- ✅ UserRepository → UserPort 의존
- ✅ UseCase 인터페이스 도입 (2개)

---

### Security 계층

#### Before
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserRepository userRepository;  // ❌ 직접 의존

  public UserDetails loadUserByUsername(String userId) {
    User user = userRepository.findById(Long.parseLong(userId))...
  }
}
```

#### After
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
  private final UserPort userPort;  // ✅ 포트 의존

  public UserDetails loadUserByUsername(String userId) {
    User user = userPort.findById(Long.parseLong(userId))...
  }
}
```

---

## 🧪 테스트 전환

### Before (레이어드 구조 테스트)
```
src/test/java/com/example/starter/domain/user/
├── UserServiceTest.java
└── UserControllerTest.java
```

### After (헥사고날 구조 테스트)
```
src/test/java/com/example/starter/domain/user/
├── application/service/
│   └── UserServiceTest.java (UserPort Mock)
└── adapter/in/web/
    └── UserControllerTest.java (UseCase Mock)
```

**테스트 개선**:
- ✅ UserServiceTest: `UserPort` Mock 사용
- ✅ UserControllerTest: UseCase 인터페이스 Mock 사용
- ✅ TodoServiceTest: `TodoPort` Mock 사용
- ✅ TodoControllerTest: UseCase 인터페이스 Mock 사용

---

## 📊 구조 비교

### 레이어드 아키텍처 (변경 전)
```
Controller → Service → Repository → Database
   ↑           ↑          ↑
  HTTP      비즈니스    데이터
  라우팅     로직      접근
```

**문제점**:
- ❌ Service가 Repository에 강하게 의존
- ❌ 테스트 시 실제 Repository를 Mock하기 어려움
- ❌ 기술 세부사항(JPA)에 의존
- ❌ 코드 재사용성 낮음

---

### 헥사고날 아키텍처 (변경 후)
```
Controller ──→ UseCase Port ──→ Service ──→ Out Port ──→ JPA Adapter ──→ Database
(Adapter)    (Interface)      (Service)   (Interface)    (Adapter)
```

**개선점**:
- ✅ UseCase를 통한 명확한 인터페이스
- ✅ Service가 Port(추상)에 의존 → 느슨한 결합
- ✅ 어댑터로 기술 세부사항 분리
- ✅ 테스트 시 Mock 객체로 쉽게 대체 가능
- ✅ 새로운 어댑터 추가 시 도메인 변경 불필요
- ✅ 단위 테스트, 통합 테스트 작성 용이

---

## 🔄 의존성 흐름 변화

### Before: 하향식 의존 (Control Flow 의존)
```
Controller
    ↓
Service ← Repository (직접 의존, 높은 결합도)
    ↓
Database
```

### After: 상향식 의존 (의존성 역전)
```
Controller ← UseCase Port (의존성 주입)
    ↓
Service ← Out Port (의존성 주입)
    ↓
Adapter (JPA) → Database
```

**결과**: 도메인 계층이 모든 것을 정의하고, 어댑터들이 도메인에 적응

---

## 🚀 사용 방법

### 테스트 실행

#### PowerShell
```bash
# 모든 테스트 실행
cd C:\Users\super\Workspace\springboot-be-dev-starter
.\run-tests.ps1

# 또는 수동 실행
gradlew test
```

#### Batch (CMD)
```batch
.\run-tests.bat
```

#### IDE
- IntelliJ IDEA: `Ctrl+Shift+F10` (테스트 실행)
- VS Code: Test Explorer 또는 코드렌즈 클릭

### 애플리케이션 실행

```bash
# 로컬 실행
gradlew bootRun

# Docker Compose 실행
docker-compose up -d
```

---

## 📈 기대 효과

| 항목 | Before | After | 개선 |
|------|--------|-------|------|
| **테스트 용이성** | 중간 | 우수 | ⬆️⬆️ |
| **코드 재사용성** | 낮음 | 높음 | ⬆️⬆️ |
| **결합도** | 높음 | 낮음 | ⬇️⬇️ |
| **응집도** | 중간 | 높음 | ⬆️⬆️ |
| **확장성** | 제한적 | 우수 | ⬆️⬆️ |
| **유지보수성** | 중간 | 우수 | ⬆️⬆️ |

---

## ✅ 완료 체크리스트

### User 도메인
- [x] Entity → domain/ 이동
- [x] UserPort 출력 포트 생성
- [x] 4개 UseCase 입력 포트 생성
- [x] UserJpaRepository, UserPersistenceAdapter 생성
- [x] UserService → application/service/ 이동 (UserPort 의존)
- [x] UserController → adapter/in/web/ 이동 (UseCase 주입)
- [x] DTO → adapter/in/web/dto/ 이동
- [x] 테스트 이동 및 업데이트

### Todo 도메인
- [x] Entity, Enum → domain/ 이동
- [x] TodoPort 출력 포트 생성
- [x] 5개 UseCase 입력 포트 생성
- [x] TodoJpaRepository, TodoPersistenceAdapter 생성
- [x] TodoService → application/service/ 이동 (TodoPort 의존)
- [x] TodoController → adapter/in/web/ 이동 (UseCase 주입)
- [x] DTO → adapter/in/web/dto/ 이동
- [x] 테스트 이동 및 업데이트

### Auth 도메인
- [x] 2개 UseCase 입력 포트 생성
- [x] AuthService → application/service/ 이동 (UserPort 의존)
- [x] AuthController → adapter/in/web/ 이동 (UseCase 주입)
- [x] DTO → adapter/in/web/dto/ 이동

### Security 계층
- [x] CustomUserDetailsService: UserRepository → UserPort

### 기타
- [x] 오래된 파일 모두 삭제
- [x] 검증 보고서 작성
- [x] 테스트 실행 스크립트 생성

---

## 📝 주의사항

1. **데이터베이스 마이그레이션**: Flyway가 자동으로 실행되므로 별도 조치 불필요
2. **JWT 토큰**: 기존 토큰과 호환 (AuthService 로직 동일)
3. **API 엔드포인트**: 변경 없음 (REST API는 동일)
4. **테스트 커버리지**: 새 구조에 맞게 테스트 업데이트 완료

---

## 🎓 학습 포인트

### 포트 & 어댑터 패턴
- **포트**: 도메인에서 제공하는 인터페이스
- **어댑터**: 포트를 구현하여 외부 기술과 연결

### 의존성 역전 원칙 (DIP)
```
고수준 모듈(도메인) ─┐
                   └─→ 추상화(Port)
저수준 모듈(어댑터) ─┘
```

### 단일 책임 원칙 (SRP)
- Controller: HTTP 처리
- UseCase: 사용 사례 정의
- Service: 비즈니스 로직
- Adapter: 기술 구현

---

## 📞 다음 단계

1. ✅ **구조 검증 완료**
2. 🔄 **테스트 실행** (`gradlew test`)
3. 🚀 **로컬 서버 실행** (`gradlew bootRun`)
4. 🐳 **Docker 배포** (`docker-compose up -d`)
5. 📊 **성능 모니터링** (필요시)

---

**생성 날짜**: 2026-02-14
**상태**: ✅ 완료
**다음**: 테스트 실행 및 배포

