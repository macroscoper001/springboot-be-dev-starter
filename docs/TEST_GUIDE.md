# 테스트 실행 가이드

이 문서는 Spring Boot 프로젝트의 Gradle 기반 테스트 실행 방법을 설명합니다.

## 필수 요구사항

### 1. Java 21 설치

프로젝트는 Java 21이 필요합니다.

**Windows:**
- [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) 다운로드 및 설치
- 또는 [OpenJDK 21](https://openjdk.java.net/projects/jdk/21/) 다운로드

설치 후 `java -version` 명령으로 확인:
```bash
java -version
```

### 2. Gradle Wrapper 설정

프로젝트에는 Gradle Wrapper 파일이 포함되어 있습니다:
- `gradlew` - Linux/Mac용 스크립트
- `gradlew.bat` - Windows용 배치 파일
- `gradle/wrapper/gradle-wrapper.properties` - Gradle 버전 설정

첫 실행 시 Gradle 자동 다운로드됩니다.

## 테스트 실행 방법

### 모든 테스트 실행

```bash
# Linux/Mac
./gradlew test

# Windows
gradlew.bat test
```

### 특정 테스트 클래스만 실행

```bash
# Linux/Mac
./gradlew test --tests UserServiceTest

# Windows
gradlew.bat test --tests UserServiceTest
```

### 특정 테스트 메서드 실행

```bash
# Linux/Mac
./gradlew test --tests UserServiceTest.testCreateUser_Success

# Windows
gradlew.bat test --tests UserServiceTest.testCreateUser_Success
```

### 도메인별 테스트 실행

```bash
# User 도메인 테스트
./gradlew test --tests "**/domain/user/**"

# Todo 도메인 테스트
./gradlew test --tests "**/domain/todo/**"
```

### 상세 출력으로 테스트 실행

```bash
./gradlew test --info
```

## 테스트 구조

### 단위 테스트 (Unit Test)
- **위치**: `src/test/java/domain/{domain}/application/service/`
- **목적**: Out Port를 Mock으로 주입하여 서비스 비즈니스 로직 테스트
- **예**: `UserServiceTest`, `TodoServiceTest`

### 통합 테스트 (Integration Test)
- **위치**: `src/test/java/domain/{domain}/adapter/in/web/`
- **목적**: MockMvc와 UseCase Mock을 사용한 컨트롤러 엔드포인트 테스트
- **예**: `UserControllerTest`, `TodoControllerTest`

## 테스트 설정

### 테스트 프로파일
테스트는 `application-test.yml` 설정을 사용합니다:
- **데이터베이스**: H2 인메모리 데이터베이스
- **Flyway**: 비활성화
- **자동 스키마 생성**: `ddl-auto: create-drop`

### 테스트 데이터베이스
테스트 실행 시 각 테스트마다 새로운 H2 데이터베이스가 생성되고, 테스트 완료 후 자동으로 삭제됩니다.

## 테스트 의존성

```gradle
// JUnit 5
testImplementation 'org.springframework.boot:spring-boot-starter-test'

// Mockito
// spring-boot-starter-test에 포함됨

// Spring Security Test
testImplementation 'org.springframework.security:spring-security-test'

// Test Database
testRuntimeOnly 'com.h2database:h2'

// Testcontainers (선택사항)
testImplementation 'org.testcontainers:testcontainers:1.19.6'
testImplementation 'org.testcontainers:postgresql:1.19.6'
```

## 테스트 실행 예제

### 전체 테스트 실행

```bash
./gradlew test
```

**출력:**
```
> Task :test

  UserServiceTest
    사용자 서비스 테스트
      ✓ 사용자 생성 - 성공
      ✓ 사용자 생성 - 이메일 중복
      ✓ 사용자 조회 - 성공
      ✓ 사용자 조회 - 사용자 없음

  TodoServiceTest
    할일 서비스 테스트
      ✓ 할일 생성 - 성공
      ✓ 할일 조회 - 성공
      ✓ 할일 조회 - 할일 없음
      ...

  UserControllerTest
    사용자 컨트롤러 테스트
      ✓ 사용자 생성 - 성공
      ✓ 사용자 조회 - 성공

  TodoControllerTest
    할일 컨트롤러 테스트
      ✓ 할일 생성 - 성공
      ✓ 할일 조회 - 성공

BUILD SUCCESSFUL in 15s
```

## 문제 해결

### 1. "command not found: gradlew"

**원인**: gradlew 파일이 실행 권한이 없습니다.

**해결**:
```bash
chmod +x gradlew
```

### 2. "JAVA_HOME is not set"

**원인**: Java가 설치되지 않았거나 JAVA_HOME 환경변수가 설정되지 않았습니다.

**해결**:
1. Java 21 설치 (위의 필수 요구사항 참고)
2. JAVA_HOME 환경변수 설정

**Windows에서 JAVA_HOME 설정:**
```cmd
setx JAVA_HOME "C:\Program Files\Java\jdk-21"
```

### 3. 테스트 실패: "Cannot load driver class: org.postgresql.Driver"

**원인**: 테스트 프로파일이 로드되지 않아 PostgreSQL 설정을 사용하려고 합니다.

**해결**: 테스트 클래스에 `@ActiveProfiles("test")` 어노테이션이 있는지 확인합니다.

### 4. 빌드 캐시 초기화

```bash
./gradlew clean
./gradlew build
./gradlew test
```

## 지속적 통합 (CI)

GitHub Actions에서 사용할 수 있는 설정:

```yaml
name: Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3
      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
      - name: Run tests
        run: ./gradlew test
```

## 추가 정보

- [Gradle 공식 문서](https://docs.gradle.org/)
- [Spring Boot 테스트 가이드](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 사용자 가이드](https://junit.org/junit5/docs/current/user-guide/)
