---
name: hexagonal-new-domain
description: 헥사고날 아키텍처 기반 새 도메인 전체 스캐폴딩. 새 도메인 추가, 도메인 스캐폴딩, 헥사고날 구조 생성 요청 시 사용.
tools: Read, Write, Edit, Glob, Grep, Bash
argument-hint: <도메인명>
model: sonnet
---

# 새 도메인 생성 (헥사고날 아키텍처)

## 입력 처리

도메인명을 파싱합니다. 예: `product` → `Product` (첫 글자 대문자)

**기본 변수**:
```
원본 입력: ${ARGUMENTS[0]}
도메인명 (소문자): ${ARGUMENTS[0] | lowercase}
도메인명 (첫글자 대문자): ${ARGUMENTS[0] | capitalize}
패키지명: com.example.starter.domain.${ARGUMENTS[0] | lowercase}
```

## Phase 1: 사전 정보 수집

### 1단계: Flyway 최신 버전 확인

```bash
ls -1 src/main/resources/db/migration/ | sort
```

마지막 파일에서 버전 번호를 추출합니다. (예: V2가 마지막이면 다음은 V3)

### 2단계: 사용자에게 질문

다음 4가지를 질문하여 답변을 수집합니다:

#### 질문 1: 엔티티 필드
```
"생성할 엔티티의 필드를 입력하세요 (예: title:String, price:Long, stock:int)"

답변 형식 분석:
- 쉼표(,)로 분리
- 각 필드는 fieldName:Type 형식
- 공백 제거
- 기본 타입: String, Long, Integer, Double, Boolean, LocalDateTime, LocalDate
```

#### 질문 2: 다중 테넌트 여부
```
"이 도메인은 사용자 소유권(userId 기반)을 지원해야 하나요? (예: Todo처럼)"

선택지:
- "네, userId 기반 다중 테넌트 필요"
- "아니요, 전역 리소스"
```

#### 질문 3: 상태 Enum 필요
```
"상태 Enum이 필요한가요? (예: TodoStatus = PENDING, COMPLETED)"

선택지:
- "필요함, 상태값을 입력하세요 (예: DRAFT, PUBLISHED, ARCHIVED)"
- "필요없음"
```

#### 질문 4: 추가 UseCase
```
"기본 CRUD(Create, Get, Update, Delete) 외 추가 UseCase가 있나요?"

답변 형식:
- 쉼표로 분리된 메서드명 (예: "publish, archive, restore")
- 또는 "없음"
```

## Phase 2: 파일 생성 순서 (TDD 기반)

**순서**: Command → Result → Port → Service → Adapter → Entity → Test

13개 파일을 다음 순서대로 생성합니다:

1. **Command 클래스들** - `Create${Name}Command`, `Update${Name}Command`
2. **Result 클래스** - `${Name}Result`
3. **UseCase 인터페이스들** - `Create/Get/Update/Delete${Name}UseCase` + 사용자 정의
4. **Out Port 인터페이스** - `${Name}Port`
5. **Service 클래스** - `${Name}Service` (모든 UseCase 구현)
6. **Request DTO** - `${Name}Request`
7. **Response DTO** - `${Name}Response`
8. **Controller** - `${Name}Controller`
9. **Entity** - `${Name}` + `${Name}Status` Enum
10. **JPA Repository** - `${Name}JpaRepository`
11. **Persistence Adapter** - `${Name}PersistenceAdapter`
12. **Flyway 마이그레이션** - `V${NEXT}__create_${domain}s_table.sql`
13. **ErrorCode 업데이트** - `common/exception/ErrorCode.java`

각 파일의 상세 템플릿은 `references/phase2-file-templates.md`를 참조하세요.

## Phase 3: 테스트 파일 생성 (선택사항)

- **Service 테스트** - `${Name}ServiceTest.java`
- **Controller 테스트** - `${Name}ControllerTest.java`

상세 템플릿은 `references/phase3-test-templates.md`를 참조하세요.

## Phase 4: 검증

### 1단계: 테스트 실행
```bash
./gradlew test --tests "${Name}ServiceTest"
./gradlew test --tests "${Name}ControllerTest"
```

### 2단계: 애플리케이션 실행
```bash
docker-compose up -d
./gradlew bootRun
```

### 3단계: Swagger 확인
- UI: http://localhost:8080/swagger-ui.html
- `/api/v1/${domain}s` 엔드포인트가 표시되는지 확인

### 4단계: cURL 테스트 (선택사항)
```bash
# 토큰 획득 (예: 이미 있는 사용자)
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"password"}' \
  | jq -r '.data.accessToken')

# ${Name} 생성
curl -X POST http://localhost:8080/api/v1/${domain}s \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '${fields.test_curl_body}'
```

---

## 완료 체크리스트

- [ ] 모든 Command 클래스 생성 완료
- [ ] Result 클래스 생성 완료
- [ ] UseCase 인터페이스 생성 완료
- [ ] Out Port 인터페이스 생성 완료
- [ ] Service 클래스 생성 완료
- [ ] Request DTO 생성 완료
- [ ] Response DTO 생성 완료
- [ ] Controller 생성 완료
- [ ] Entity 생성 완료 (Enum 포함)
- [ ] JPA Repository 생성 완료
- [ ] Persistence Adapter 생성 완료
- [ ] Flyway 마이그레이션 생성 완료
- [ ] ErrorCode 업데이트 완료
- [ ] Service 테스트 통과
- [ ] Controller 테스트 통과
- [ ] Swagger에 엔드포인트 표시됨
