# 프로젝트 구조 및 파일 구성

이 문서는 프로젝트의 최종 정리된 구조를 설명합니다.

## 📁 프로젝트 디렉토리 구조

```
springboot-be-dev-starter/
│
├── 📂 docs/                                 # 📌 문서 디렉토리
│   ├── PROJECT_STRUCTURE.md                # 이 파일
│   ├── TEST_GUIDE.md                       # 테스트 상세 가이드
│   ├── QUICKSTART_TESTS.md                 # 5분 빠른 시작
│   ├── GRADLE_SETUP_SUMMARY.md             # Gradle 설정 요약
│   ├── HEXAGONAL_ARCHITECTURE_VERIFICATION.md
│   └── HEXAGONAL_IMPLEMENTATION_SUMMARY.md
│
├── 📂 gradle/                              # Gradle Wrapper
│   └── wrapper/
│       ├── gradle-wrapper.properties
│       └── gradle-wrapper.jar              # (자동 다운로드됨)
│
├── 📂 src/
│   ├── main/
│   │   ├── java/com/example/starter/
│   │   │   ├── StarterApplication.java
│   │   │   ├── config/                    # Spring 설정
│   │   │   ├── security/                  # JWT 인증
│   │   │   ├── common/                    # 공통 유틸
│   │   │   └── domain/                    # 도메인 계층 (헥사고날)
│   │   │       ├── auth/
│   │   │       ├── user/
│   │   │       └── todo/
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-local.yml
│   │       ├── application-test.yml       # 테스트 설정
│   │       └── db/migration/              # Flyway 마이그레이션
│   │
│   └── test/
│       ├── java/com/example/starter/
│       │   └── domain/                    # 도메인 테스트
│       │       ├── user/
│       │       │   ├── application/service/UserServiceTest.java
│       │       │   └── adapter/in/web/UserControllerTest.java
│       │       └── todo/
│       │           ├── application/service/TodoServiceTest.java
│       │           └── adapter/in/web/TodoControllerTest.java
│       └── resources/
│           └── application-test.yml       # 테스트용 H2 설정
│
├── 🔧 빌드 및 설정 파일
│   ├── build.gradle                       # Gradle 설정 (의존성, 플러그인)
│   ├── settings.gradle                    # 프로젝트명 설정
│   ├── gradle.properties                  # Gradle 성능 설정
│   ├── .gitignore                         # Git 무시 규칙
│   ├── CLAUDE.md                          # 프로젝트 개발 가이드 ⭐
│   └── README.md                          # 프로젝트 개요
│
├── 🐳 컨테이너 설정
│   ├── Dockerfile                         # Docker 이미지 정의
│   ├── docker-compose.yml                 # Docker 오케스트레이션
│   └── .env.example                       # 환경 변수 템플릿
│
└── 🚀 실행 스크립트
    ├── gradlew                            # Linux/Mac Gradle Wrapper
    ├── gradlew.bat                        # Windows Gradle Wrapper
    ├── run-tests.bat                      # Windows 테스트 실행
    └── run-tests.ps1                      # PowerShell 테스트 실행
```

## 📄 주요 파일 설명

### 최상위 파일

| 파일 | 설명 |
|------|------|
| **CLAUDE.md** | ⭐ 프로젝트 개발 가이드 - 아키텍처, 개발 방법, 중요 규칙 |
| **README.md** | 프로젝트 개요, 빠른 시작, API 문서 |
| **build.gradle** | Gradle 빌드 설정, 의존성 관리 |
| **docker-compose.yml** | PostgreSQL + 앱 컨테이너 실행 설정 |
| **Dockerfile** | Docker 멀티스테이지 빌드 설정 |
| **.env.example** | 환경 변수 템플릿 |

### 문서 디렉토리 (`docs/`)

| 파일 | 목적 |
|------|------|
| **TEST_GUIDE.md** | 테스트 실행 상세 가이드 |
| **QUICKSTART_TESTS.md** | 5분 안에 테스트 실행하기 |
| **GRADLE_SETUP_SUMMARY.md** | Gradle Wrapper 설정 완료 요약 |
| **HEXAGONAL_ARCHITECTURE_VERIFICATION.md** | 헥사고날 아키텍처 검증 |
| **HEXAGONAL_IMPLEMENTATION_SUMMARY.md** | 헥사고날 아키텍처 구현 요약 |

## 🗂️ 소스 코드 구조 (헥사고날 아키텍처)

### 각 도메인의 표준 구조

```
domain/{domain-name}/
├── domain/
│   ├── {Entity}.java                      # JPA Entity
│   └── {Enum}.java                        # 상태 열거형 (필요시)
│
├── application/
│   ├── port/
│   │   ├── in/
│   │   │   ├── Create{Entity}UseCase.java
│   │   │   ├── Get{Entity}UseCase.java
│   │   │   ├── Update{Entity}UseCase.java
│   │   │   └── Delete{Entity}UseCase.java
│   │   └── out/
│   │       └── {Entity}Port.java          # 저장소 포트
│   └── service/
│       └── {Entity}Service.java           # UseCase 구현체
│
└── adapter/
    ├── in/web/
    │   ├── {Entity}Controller.java
    │   └── dto/
    │       ├── {Entity}Request.java
    │       └── {Entity}Response.java
    └── out/persistence/
        ├── {Entity}JpaRepository.java
        └── {Entity}PersistenceAdapter.java
```

## 🧪 테스트 구조

```
src/test/java/com/example/starter/
└── domain/
    ├── user/
    │   ├── application/service/
    │   │   └── UserServiceTest.java       # 단위 테스트
    │   └── adapter/in/web/
    │       └── UserControllerTest.java    # 통합 테스트
    └── todo/
        ├── application/service/
        │   └── TodoServiceTest.java       # 단위 테스트
        └── adapter/in/web/
            └── TodoControllerTest.java    # 통합 테스트
```

**테스트 특징:**
- 단위 테스트: Out Port를 Mock으로 사용
- 통합 테스트: MockMvc로 HTTP 요청/응답 검증
- 테스트 DB: H2 인메모리 (자동 생성/삭제)

## 🔧 설정 파일

### `application.yml` (프로덕션)
- PostgreSQL 데이터베이스 설정
- Flyway 마이그레이션 활성화
- JWT 토큰 설정

### `application-local.yml` (로컬 개발)
- 로컬 PostgreSQL 설정
- 상세 로깅 활성화

### `application-test.yml` (테스트)
- H2 인메모리 데이터베이스
- Flyway 비활성화
- 자동 스키마 생성 (`create-drop`)

## 📦 의존성 구조

```
Spring Boot 3.3.0
├── Spring Web (REST API)
├── Spring Data JPA (ORM)
├── Spring Security (인증/인가)
│   └── JJWT 0.12.3 (JWT 토큰)
├── PostgreSQL (프로덕션 DB)
├── H2 (테스트 DB)
├── Flyway (DB 마이그레이션)
├── SpringDoc OpenAPI (Swagger UI)
├── Lombok (코드 생성)
└── MapStruct (DTO 매핑)

Test Dependencies
├── JUnit 5 (테스트 프레임워크)
├── Mockito (Mock 객체)
├── Spring Security Test (인증 테스트)
└── Testcontainers (실제 DB 테스트)
```

## 🚀 실행 방법

### 테스트 실행

**Windows:**
```bash
# 자동화 배치 파일
run-tests.bat

# 또는 Gradle 직접 실행
gradlew.bat test
```

**Linux/Mac:**
```bash
./gradlew test
```

### 애플리케이션 실행

**Docker Compose (PostgreSQL + App):**
```bash
docker-compose up -d
```

**로컬 개발 (PostgreSQL 필수):**
```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

## 📋 정리 이력

### 2026-02-14: 최종 프로젝트 정리
- ✅ 문서 통합: 모든 가이드 문서를 `docs/` 디렉토리로 이동
- ✅ 스크립트 정리: 미사용 스크립트 제거
  - `init-gradle-wrapper.bat/sh` (run-tests에 통합됨)
  - `Test-Project.ps1` (이전 버전)
  - `test-quick.bat` (이전 버전)
- ✅ README.md 업데이트: 문서 참조 추가
- ✅ .gitignore 개선: gradle/wrapper/ 포함 명시

## 📖 다음 단계

1. **CLAUDE.md 읽기**: 프로젝트의 상세 가이드 확인
2. **docs/ 문서 참조**: 특정 주제별 가이드 선택
3. **테스트 실행**: `run-tests.bat` 또는 `./gradlew test`
4. **로컬 개발**: Docker Compose 또는 로컬 PostgreSQL로 시작

---

**마지막 업데이트**: 2026-02-14 ✅
