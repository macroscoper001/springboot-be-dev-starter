# 🚀 테스트 빠른 시작 가이드

5분 안에 Gradle 기반 테스트를 실행하세요!

## 1단계: Java 21 설치 확인

```bash
java -version
```

설치되지 않았으면:
- [Oracle JDK 21](https://www.oracle.com/java/technologies/downloads/#java21) 또는
- [OpenJDK 21](https://openjdk.java.net/projects/jdk/21/) 설치

## 2단계: Gradle Wrapper 초기화

### Windows:
```bash
init-gradle-wrapper.bat
```

### Linux/Mac:
```bash
chmod +x init-gradle-wrapper.sh
./init-gradle-wrapper.sh
```

> 이 스크립트는 gradle-wrapper.jar를 자동으로 다운로드합니다.

## 3단계: 테스트 실행

### 모든 테스트 실행:
```bash
# Windows
gradlew.bat test

# Linux/Mac
./gradlew test
```

### 특정 테스트 실행:
```bash
# Windows - UserService 테스트
gradlew.bat test --tests UserServiceTest

# Linux/Mac - TodoService 테스트
./gradlew test --tests TodoServiceTest
```

## 예상 결과

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
    ✓ 사용자의 할일 목록 조회 - 성공
    ✓ 할일 수정 - 성공
    ✓ 할일 삭제 - 성공
    ✓ 할일 완료 처리 - 성공

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

## 일반적인 명령어

| 명령어 | 설명 |
|--------|------|
| `gradlew test` | 모든 테스트 실행 |
| `gradlew test --tests UserServiceTest` | 특정 테스트 클래스 실행 |
| `gradlew test --tests "*ServiceTest"` | 패턴 매칭 테스트 |
| `gradlew test --info` | 상세 출력 |
| `gradlew clean` | 빌드 캐시 초기화 |
| `gradlew build` | 프로젝트 빌드 (테스트 포함) |

## 문제 해결

| 문제 | 해결 방법 |
|------|---------|
| `command not found: gradlew` | `chmod +x gradlew` 실행 |
| `JAVA_HOME is not set` | Java 21 설치 후 환경변수 설정 |
| 테스트 실패 (PostgreSQL) | 테스트는 H2를 사용하므로 DB 필요 없음 |
| gradle-wrapper.jar 다운로드 실패 | `init-gradle-wrapper.bat/sh` 다시 실행 |

## 더 알아보기

자세한 내용은 [TEST_GUIDE.md](TEST_GUIDE.md)를 참조하세요.

---

**팁**: 처음 실행 시 Gradle 다운로드 때문에 시간이 걸릴 수 있습니다. 이후 실행은 훨씬 빠릅니다! ⚡
