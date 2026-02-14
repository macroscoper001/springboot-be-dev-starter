# SPRINGBOOT-BE-DEV-STARTER

Spring Boot RESTful API 서버 스타터 킷 (Java 21 + Spring Boot 3.x + PostgreSQL + JWT 인증)

## 기술 스택

- **Java 21** (LTS)
- **Spring Boot 3.3.x**
- **Spring Security 6.x**
- **Spring Data JPA 3.x**
- **PostgreSQL 16**
- **JWT (JJWT 0.12.x)**
- **Flyway** (DB 마이그레이션)
- **SpringDoc OpenAPI** (Swagger UI)
- **Lombok**
- **Docker & Docker Compose**
- **Gradle 8.x**

## 빠른 시작

### 1. 환경 설정

```bash
# .env.example 파일을 복사하여 .env 파일 생성
cp .env.example .env
```

### 2. 필요한 환경 변수 설정

```.env
DB_HOST=localhost
DB_PORT=5432
DB_NAME=starter_db
DB_USER=starter_user
DB_PASSWORD=starter_password
JWT_SECRET=your-secret-key-at-least-32-characters-long-for-HS256
SERVER_PORT=8080
```

### 3. Docker Compose로 실행

```bash
# PostgreSQL + 앱 컨테이너 실행
docker-compose up -d
```

### 4. 애플리케이션 확인

- **API 문서**: http://localhost:8080/swagger-ui.html
- **Health Check**: http://localhost:8080/actuator/health

## 주요 기능

### JWT 인증 흐름

```
POST /api/v1/auth/login
  ↓
JWT Access Token + Refresh Token 발급
  ↓
Authorization: Bearer <token> 헤더로 보호된 API 접근
  ↓
POST /api/v1/auth/refresh
  ↓
새로운 Access Token 발급
```

### API 엔드포인트

#### 인증 API

```
POST /api/v1/auth/login
  - 요청: { "username": "...", "password": "..." }
  - 응답: { "accessToken": "...", "refreshToken": "..." }

POST /api/v1/auth/refresh
  - 요청: { "refreshToken": "..." }
  - 응답: { "accessToken": "...", "refreshToken": "..." }
```

#### 사용자 API

```
POST /api/v1/users
  - 사용자 생성

GET /api/v1/users
  - 모든 사용자 조회 (페이징)

GET /api/v1/users/{userId}
  - 사용자 상세 조회

PUT /api/v1/users/{userId}
  - 사용자 정보 업데이트

DELETE /api/v1/users/{userId}
  - 사용자 삭제
```

## 프로젝트 구조

```
be-dev-starter/
├── build.gradle                 # 의존성 및 빌드 설정
├── docker-compose.yml           # PostgreSQL + 앱
├── Dockerfile                   # 멀티스테이지 빌드
├── .env.example                 # 환경 변수 템플릿
│
└── src/
    ├── main/
    │   ├── java/com/example/starter/
    │   │   ├── StarterApplication.java
    │   │   ├── config/
    │   │   │   ├── SecurityConfig.java
    │   │   │   ├── SwaggerConfig.java
    │   │   │   └── JpaAuditConfig.java
    │   │   ├── security/
    │   │   │   ├── JwtTokenProvider.java
    │   │   │   ├── JwtAuthFilter.java
    │   │   │   ├── JwtProperties.java
    │   │   │   └── CustomUserDetailsService.java
    │   │   ├── common/
    │   │   │   ├── response/ApiResponse.java
    │   │   │   ├── exception/
    │   │   │   │   ├── BusinessException.java
    │   │   │   │   ├── ErrorCode.java
    │   │   │   │   └── GlobalExceptionHandler.java
    │   │   │   └── entity/BaseEntity.java
    │   │   └── domain/
    │   │       ├── auth/
    │   │       │   ├── AuthController.java
    │   │       │   ├── AuthService.java
    │   │       │   └── dto/
    │   │       └── user/
    │   │           ├── UserController.java
    │   │           ├── UserService.java
    │   │           ├── UserRepository.java
    │   │           ├── User.java
    │   │           └── dto/
    │   └── resources/
    │       ├── application.yml
    │       ├── application-local.yml
    │       └── db/migration/V1__init_schema.sql
    │
    └── test/
        └── java/com/example/starter/
            ├── StarterApplicationTests.java
            ├── domain/user/
            │   ├── UserServiceTest.java
            │   └── UserControllerTest.java
```

## 로컬 개발 방법

### Gradle 빌드

```bash
# 프로젝트 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 애플리케이션 실행
./gradlew bootRun
```

### PostgreSQL 직접 실행 (Docker 없이)

```bash
# Docker로 PostgreSQL만 실행
docker run -d \
  --name starter_postgres \
  -e POSTGRES_DB=starter_db \
  -e POSTGRES_USER=starter_user \
  -e POSTGRES_PASSWORD=starter_password \
  -p 5432:5432 \
  postgres:16-alpine

# application-local.yml 프로필로 애플리케이션 실행
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 환경 프로필

- **default**: Docker Compose 환경
- **local**: 로컬 개발 환경 (상세 로깅)

프로필 설정:
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 테스트

### 테스트 실행

**Windows:**
```bash
# 자동화된 배치 파일 (권장)
run-tests.bat

# 또는 Gradle 직접 실행
gradlew.bat test
```

**Linux/Mac:**
```bash
./gradlew test
```

자세한 테스트 가이드는 [`docs/TEST_GUIDE.md`](docs/TEST_GUIDE.md)를 참조하세요.

### 특정 테스트만 실행

```bash
# User 도메인 테스트
gradlew test --tests UserServiceTest

# Todo 도메인 테스트
gradlew test --tests TodoServiceTest
```

## JWT 토큰 예제

### 로그인

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password123"}'
```

응답:
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGc...",
    "refreshToken": "eyJhbGc...",
    "tokenType": "Bearer",
    "expiresIn": 3600
  },
  "message": "로그인에 성공했습니다",
  "timestamp": "2024-01-15T10:30:00"
}
```

### 인증이 필요한 API 호출

```bash
curl -X GET http://localhost:8080/api/v1/users/1 \
  -H "Authorization: Bearer eyJhbGc..."
```

## 데이터베이스 마이그레이션

Flyway를 사용하여 데이터베이스 스키마를 자동으로 관리합니다.

마이그레이션 파일 위치: `src/main/resources/db/migration/`

새 마이그레이션 추가:
```
V2__add_new_table.sql
V3__modify_column.sql
```

## 보안

- **암호**: BCryptPasswordEncoder로 인코딩
- **JWT**: HS256 서명 알고리즘
- **Access Token 유효시간**: 1시간
- **Refresh Token 유효시간**: 7일
- **CORS**: 필요에 따라 설정 추가
- **CSRF**: REST API용 비활성화

## 에러 응답 형식

```json
{
  "success": false,
  "message": "에러 메시지",
  "timestamp": "2024-01-15T10:30:00"
}
```

## 공통 응답 형식

```json
{
  "success": true,
  "data": { /* 데이터 */ },
  "message": "성공 메시지",
  "timestamp": "2024-01-15T10:30:00"
}
```

## 문제 해결

### PostgreSQL 연결 오류

```bash
# PostgreSQL 컨테이너 상태 확인
docker ps | grep postgres

# 로그 확인
docker logs starter_postgres
```

### 포트 충돌

포트가 이미 사용 중인 경우 `.env` 파일에서 포트 변경:

```
DB_PORT=5433  # 기본값 5432에서 변경
SERVER_PORT=8081  # 기본값 8080에서 변경
```

## 📚 문서

### 프로젝트 가이드

프로젝트의 상세한 설명은 [`CLAUDE.md`](CLAUDE.md)를 참조하세요.

### 문서 목록 (`docs/` 디렉토리)

| 문서 | 설명 |
|------|------|
| [`TEST_GUIDE.md`](docs/TEST_GUIDE.md) | 테스트 실행 가이드 |
| [`QUICKSTART_TESTS.md`](docs/QUICKSTART_TESTS.md) | 5분 빠른 시작 가이드 |
| [`GRADLE_SETUP_SUMMARY.md`](docs/GRADLE_SETUP_SUMMARY.md) | Gradle 설정 요약 |
| [`HEXAGONAL_ARCHITECTURE_VERIFICATION.md`](docs/HEXAGONAL_ARCHITECTURE_VERIFICATION.md) | 헥사고날 아키텍처 검증 |
| [`HEXAGONAL_IMPLEMENTATION_SUMMARY.md`](docs/HEXAGONAL_IMPLEMENTATION_SUMMARY.md) | 헥사고날 아키텍처 구현 요약 |

## 라이센스

MIT

## 기여

버그 리포트 및 개선 제안은 이슈를 통해 등록해주세요.
