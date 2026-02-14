# Security Check Command

## 목적
Spring Boot RESTful API의 보안 취약점을 체계적으로 검사하고 개선 방안을 제시합니다.

## 사용 방법
```bash
security:check
```

## 검사 항목

### 1. 의존성 취약점 검사
- **목표**: OWASP Top 10 및 알려진 CVE 확인
- **도구**: Gradle dependency check
- **명령어**:
  ```bash
  ./gradlew dependencyCheck
  ```
- **확인사항**:
  - Spring Boot 최신 버전 업데이트 필요 여부
  - 보안 패치가 필요한 의존성
  - 사용하지 않는 오래된 의존성

### 2. 코드 정적 분석 (SonarQube 규칙)
검사할 항목들:
- **SQL Injection 위험**: 매개변수화되지 않은 쿼리 (Flyway 마이그레이션, JPA Query 포함)
- **민감 정보 노출**: 평문 암호, API 키, DB 자격증명
- **하드코딩된 시크릿**: JWT_SECRET, 환경변수 하드코딩
- **약한 암호화**: BCryptPasswordEncoder 사용 여부, 적절한 strength 설정
- **보안 헤더**: HSTS, CSP, X-Frame-Options 미설정
- **CORS/CSRF**: CORS 설정 위험성, CSRF 보호 상태

### 3. 인증/인가 검사
- **JWT 설정**:
  - `jwt.secret` 길이 ≥ 32자 (HS256 최소 요구사항)
  - Access Token TTL 적절성 (1시간)
  - Refresh Token TTL 적절성 (7일)
  - Token Expiration 전략 구현 여부

- **암호 정책**:
  - BCryptPasswordEncoder 사용 여부
  - 최소 암호 길이 요구사항
  - 평문 암호 저장 여부

- **인가 검증**:
  - 행(Row) 수준 권한 부여 (User ID 검증)
  - 엔드포인트별 역할 기반 접근 제어 (@PreAuthorize 사용)

### 4. 설정 파일 검사
검사 파일:
- `.env` / `.env.example`:
  - 실제 시크릿이 `.env` 예제에 포함되지 않았는지 확인
  - 자격증명이 커밋되지 않았는지 확인

- `application.yml`:
  - `spring.jpa.hibernate.ddl-auto`: `validate` 설정 확인
  - `spring.security.csrf.enabled`: 상태 비보존 아키텍처에 맞게 설정되었는지
  - `logging.level`: 민감 정보 로깅 여부

- `docker-compose.yml`:
  - 환경변수가 `.env`에서 로드되는지 확인
  - DB 루트 암호가 강력한지 확인

### 5. Docker 보안 검사
- **Dockerfile**:
  - 멀티스테이지 빌드 사용 여부
  - 최소 JRE 이미지 사용 여부
  - 루트 사용자(USER root) 피하기
  - 불필요한 레이어 최소화

- **docker-compose.yml**:
  - 헬스 체크 구성 여부
  - 네트워크 격리 (bridge network)
  - 불필요한 포트 노출 여부

### 6. API 보안 검사
- **입력 검증**:
  - `@Valid` / `@Validated` 사용 여부
  - DTO에 제약조건(`@Size`, `@NotNull` 등) 설정
  - 개체 크기 제한 설정 여부

- **응답 보안**:
  - 민감 정보(내부 스택 트레이스, DB 구조) 노출 여부
  - GlobalExceptionHandler에서 안전한 에러 메시지 반환

- **Rate Limiting**:
  - DDoS 방어 메커니즘 (선택적)
  - API 호출 제한 정책

### 7. 로깅 보안
- **민감 정보 로깅**:
  - 사용자 암호 로깅 여부
  - 토큰 전체 로깅 여부
  - 개인 정보(PII) 로깅 여부

### 8. 데이터베이스 보안
- **연결 보안**:
  - PostgreSQL SSL 연결 여부 (프로덕션)
  - DB 암호 정책

- **마이그레이션**:
  - 초기 테이블 생성 시 기본 제약조건 설정
  - 민감 컬럼 암호화 여부

---

## 개선 방안 제시

각 문제에 대해 다음과 같이 제시합니다:

### 코드 예제
```java
// ❌ 취약한 코드
String query = "SELECT * FROM users WHERE id = " + userId;  // SQL Injection 위험
String password = "admin123";  // 평문 암호

// ✅ 개선된 코드
User user = userJpaRepository.findById(userId);  // 매개변수화 쿼리
passwordEncoder.encode(password);  // 암호화
```

### 설정 예제
```yaml
# ✅ 개선된 application.yml
spring:
  jpa:
    hibernate:
      ddl-auto: validate  # 스키마 자동 생성 비활성화
  security:
    user:
      roles: USER  # 기본 사용자 역할
```

---

## 자동 검사 스크립트

### Gradle Task 추가 (`build.gradle`)
```gradle
task securityCheck {
    doLast {
        println "🔒 보안 검사 시작..."
        exec { commandLine './gradlew', 'dependencyCheck' }
        println "✅ 보안 검사 완료"
    }
}
```

### 실행 명령어
```bash
./gradlew securityCheck
./gradlew test  # 보안 관련 테스트 포함

# OWASP Dependency Check (선택적)
./gradlew dependencyCheckAnalyze
```

---

## 체크리스트

### 프로젝트 설정
- [ ] `application.yml`에서 `ddl-auto: validate` 확인
- [ ] `.env` 파일이 `.gitignore`에 포함되어 있는지 확인
- [ ] `JWT_SECRET`이 32자 이상인지 확인
- [ ] 기본 관리자 계정이 프로덕션에서 비활성화되어 있는지 확인

### 의존성
- [ ] Spring Boot 최신 패치 버전 사용 (3.3.x)
- [ ] Spring Security 최신 버전 사용 (6.x)
- [ ] JJWT 버전 ≥ 0.12.3

### 코드
- [ ] 모든 엔드포인트에 `@PreAuthorize` 또는 `@SecurityRequirement` 설정
- [ ] 사용자 소유권 검증 구현 (userId 일치 확인)
- [ ] 모든 DTO에 입력 검증 설정

### 테스트
- [ ] 보안 테스트 케이스 작성 (권한 없는 접근 차단 등)
- [ ] 암호 암호화 테스트
- [ ] JWT 토큰 만료 테스트

### 배포
- [ ] `docker-compose.yml`에서 환경변수가 `.env`에서 로드되는지 확인
- [ ] DB 자격증명이 강력한지 확인
- [ ] 프로덕션 환경에서 불필요한 Swagger UI 비활성화 고려

---

## 참고 자료

- [OWASP Top 10](https://owasp.org/Top10/)
- [Spring Security Documentation](https://spring.io/projects/spring-security)
- [Spring Boot Security Best Practices](https://spring.io/guides/gs/securing-web/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8949)

---

## 개선 우선순위

| 우선순위 | 항목 | 영향도 | 난이도 |
|---------|------|--------|--------|
| 🔴 높음 | JWT Secret 강도 | 매우 높음 | 낮음 |
| 🔴 높음 | 암호 암호화 | 매우 높음 | 낮음 |
| 🔴 높음 | 입력 검증 | 높음 | 중간 |
| 🟡 중간 | 보안 헤더 추가 | 중간 | 낮음 |
| 🟡 중간 | 의존성 업데이트 | 중간 | 낮음 |
| 🟢 낮음 | Rate Limiting | 낮음 | 높음 |
| 🟢 낮음 | 감사 로깅 | 낮음 | 중간 |

---

**마지막 업데이트**: 2026-02-14
