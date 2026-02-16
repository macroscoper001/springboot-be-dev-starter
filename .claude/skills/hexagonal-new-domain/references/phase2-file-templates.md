# Phase 2: 파일 생성 템플릿 (13개 파일)

## 1. Command 클래스들

### Create${Name}Command

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/command/Create${Name}Command.java`

```java
package com.example.starter.domain.${domain}.application.port.in.command;

/**
 * ${Name} 생성 커맨드
 */
public record Create${Name}Command(
  ${fields.create_fields}
) {}
```

**필드 변환 규칙**:
- 사용자 입력: `title:String, description:String, price:Long`
- 생성된 코드: `String title, String description, Long price`
- ❌ userId는 Command에 포함하지 않음 (UseCase 매개변수로 전달)
- ✅ createdAt, updatedAt은 절대 포함하지 않음

### Update${Name}Command

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/command/Update${Name}Command.java`

```java
package com.example.starter.domain.${domain}.application.port.in.command;

/**
 * ${Name} 수정 커맨드
 */
public record Update${Name}Command(
  ${fields.update_fields}
) {}
```

---

## 2. Result 클래스

### ${Name}Result

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/command/${Name}Result.java`

```java
package com.example.starter.domain.${domain}.application.port.in.command;

import java.time.LocalDateTime;

/**
 * ${Name} 응답 결과 (애플리케이션 계층 전용)
 */
public record ${Name}Result(
  Long id,
  ${userId_field}
  ${fields.result_fields},
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
```

**필드 변환**:
- `id: Long id,` (항상)
- userId 필요시: `Long userId,` (다중 테넌트인 경우)
- 상태 Enum 필요시: `${Name}Status status,`
- 사용자 입력 필드들
- 항상 끝: `LocalDateTime createdAt, LocalDateTime updatedAt`

---

## 3. UseCase 인터페이스들

### Create${Name}UseCase

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/Create${Name}UseCase.java`

```java
package com.example.starter.domain.${domain}.application.port.in;

import com.example.starter.domain.${domain}.application.port.in.command.Create${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;

/**
 * ${Name} 생성 UseCase
 */
public interface Create${Name}UseCase {

  /**
   * 새로운 ${name}을 생성합니다
   */
  ${Name}Result create${Name}(${userId_param}Create${Name}Command command);
}
```

**메서드 시그니처 규칙**:
- 다중 테넌트: `${Name}Result create${Name}(Long userId, Create${Name}Command command);`
- 단일 테넌트: `${Name}Result create${Name}(Create${Name}Command command);`

### Get${Name}UseCase

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/Get${Name}UseCase.java`

```java
package com.example.starter.domain.${domain}.application.port.in;

import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ${Name} 조회 UseCase
 */
public interface Get${Name}UseCase {

  /**
   * ID로 ${name}을 조회합니다
   */
  ${Name}Result get${Name}ById(${userId_param}Long id);

  /**
   * ${name}을 페이징하여 조회합니다
   */
  Page<${Name}Result> getAll${Name}s(${userId_param_get_all}Pageable pageable);
}
```

### Update${Name}UseCase

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/Update${Name}UseCase.java`

```java
package com.example.starter.domain.${domain}.application.port.in;

import com.example.starter.domain.${domain}.application.port.in.command.Update${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;

/**
 * ${Name} 수정 UseCase
 */
public interface Update${Name}UseCase {

  /**
   * ${name}을 수정합니다
   */
  ${Name}Result update${Name}(${userId_param}Long id, Update${Name}Command command);
}
```

### Delete${Name}UseCase

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/Delete${Name}UseCase.java`

```java
package com.example.starter.domain.${domain}.application.port.in;

/**
 * ${Name} 삭제 UseCase
 */
public interface Delete${Name}UseCase {

  /**
   * ${name}을 삭제합니다
   */
  void delete${Name}(${userId_param}Long id);
}
```

### 추가 UseCase (필요시)

예: `publish` 메서드가 필요한 경우

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/in/Publish${Name}UseCase.java`

```java
package com.example.starter.domain.${domain}.application.port.in;

import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;

/**
 * ${Name} 게시 UseCase
 */
public interface Publish${Name}UseCase {

  /**
   * ${name}을 게시합니다
   */
  ${Name}Result publish${Name}(${userId_param}Long id);
}
```

---

## 4. Out Port 인터페이스

### ${Name}Port

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/port/out/${Name}Port.java`

#### 다중 테넌트 버전:

```java
package com.example.starter.domain.${domain}.application.port.out;

import com.example.starter.domain.${domain}.domain.${Name};
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ${Name} 저장소 포트 (영속성 추상화)
 */
public interface ${Name}Port {

  /**
   * ${name}을 저장합니다
   */
  ${Name} save(${Name} entity);

  /**
   * ID로 ${name}을 조회합니다 (다중 테넌트)
   */
  Optional<${Name}> findByIdAndUserId(Long id, Long userId);

  /**
   * 사용자의 모든 ${name}을 조회합니다
   */
  Page<${Name}> findByUserId(Long userId, Pageable pageable);

  /**
   * ${name}을 삭제합니다
   */
  void delete(${Name} entity);

  /**
   * ID로 ${name}을 조회합니다 (내부용)
   */
  Optional<${Name}> findById(Long id);
}
```

#### 단일 테넌트 버전:

```java
package com.example.starter.domain.${domain}.application.port.out;

import com.example.starter.domain.${domain}.domain.${Name};
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * ${Name} 저장소 포트
 */
public interface ${Name}Port {

  /**
   * ${name}을 저장합니다
   */
  ${Name} save(${Name} entity);

  /**
   * ID로 ${name}을 조회합니다
   */
  Optional<${Name}> findById(Long id);

  /**
   * 모든 ${name}을 조회합니다
   */
  Page<${Name}> findAll(Pageable pageable);

  /**
   * ${name}을 삭제합니다
   */
  void delete(${Name} entity);
}
```

---

## 5. Service 클래스

### ${Name}Service (다중 테넌트)

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/application/service/${Name}Service.java`

```java
package com.example.starter.domain.${domain}.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.${domain}.application.port.in.Create${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Get${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Update${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Delete${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.command.Create${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.Update${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import com.example.starter.domain.${domain}.application.port.out.${Name}Port;
import com.example.starter.domain.${domain}.domain.${Name};
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * ${Name} 애플리케이션 서비스 (UseCase 구현체)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ${Name}Service implements Create${Name}UseCase, Get${Name}UseCase, Update${Name}UseCase, Delete${Name}UseCase${additional_implements} {

  private final ${Name}Port ${domain}Port;

  @Override
  public ${Name}Result create${Name}(Long userId, Create${Name}Command command) {
    ${Name} entity = ${Name}.builder()
        ${fields.builder_calls}
        .userId(userId)
        .build();

    ${Name} saved = ${domain}Port.save(entity);
    log.info("${Name} 생성 완료: ${domain}Id={}, userId={}", saved.getId(), userId);

    return to${Name}Result(saved);
  }

  @Override
  @Transactional(readOnly = true)
  public ${Name}Result get${Name}ById(Long id, Long userId) {
    ${Name} entity = ${domain}Port.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.${DOMAIN}_NOT_FOUND));

    return to${Name}Result(entity);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<${Name}Result> getAll${Name}s(Long userId, Pageable pageable) {
    return ${domain}Port.findByUserId(userId, pageable)
        .map(this::to${Name}Result);
  }

  @Override
  public ${Name}Result update${Name}(Long id, Long userId, Update${Name}Command command) {
    ${Name} entity = ${domain}Port.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.${DOMAIN}_NOT_FOUND));

    ${fields.update_calls}

    ${Name} updated = ${domain}Port.save(entity);
    log.info("${Name} 수정 완료: ${domain}Id={}, userId={}", id, userId);

    return to${Name}Result(updated);
  }

  @Override
  public void delete${Name}(Long id, Long userId) {
    ${Name} entity = ${domain}Port.findByIdAndUserId(id, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.${DOMAIN}_NOT_FOUND));

    ${domain}Port.delete(entity);
    log.info("${Name} 삭제 완료: ${domain}Id={}, userId={}", id, userId);
  }

  /**
   * ${Name} 엔티티를 ${Name}Result로 변환
   */
  private ${Name}Result to${Name}Result(${Name} entity) {
    return new ${Name}Result(
      entity.getId(),
      entity.getUserId(),
      ${fields.result_calls},
      entity.getCreatedAt(),
      entity.getUpdatedAt()
    );
  }
}
```

**추가 메서드** (사용자 정의 UseCase인 경우, 예: `publish`):

```java
@Override
public ${Name}Result publish${Name}(Long id, Long userId) {
  ${Name} entity = ${domain}Port.findByIdAndUserId(id, userId)
      .orElseThrow(() -> new BusinessException(ErrorCode.${DOMAIN}_NOT_FOUND));

  entity.publish();  // 도메인 메서드 호출
  ${Name} updated = ${domain}Port.save(entity);

  log.info("${Name} 게시: ${domain}Id={}, userId={}", id, userId);

  return to${Name}Result(updated);
}
```

---

## 6. Request DTO

### ${Name}Request

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/adapter/in/web/dto/${Name}Request.java`

```java
package com.example.starter.domain.${domain}.adapter.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ${Name} HTTP 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ${Name}Request {

  ${fields.request_dtos}
}
```

**필드 생성 규칙**:
- String: `@NotBlank(message = "필드명은 필수입니다") private String fieldName;`
- Long/Integer: `@NotNull(message = "필드명은 필수입니다") private Long fieldName;`
- Boolean: `private Boolean isActive = false;` (검증 불필요)

---

## 7. Response DTO

### ${Name}Response

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/adapter/in/web/dto/${Name}Response.java`

```java
package com.example.starter.domain.${domain}.adapter.in.web.dto;

import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ${Name} HTTP 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ${Name}Response {

  private Long id;
  ${userId_response}
  ${fields.response_dtos}
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * Result에서 Response로 변환
   */
  public static ${Name}Response from${Name}Result(${Name}Result result) {
    return ${Name}Response.builder()
        .id(result.id())
        ${userId_response_builder}
        ${fields.response_builder}
        .createdAt(result.createdAt())
        .updatedAt(result.updatedAt())
        .build();
  }
}
```

---

## 8. Controller

### ${Name}Controller

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/adapter/in/web/${Name}Controller.java`

```java
package com.example.starter.domain.${domain}.adapter.in.web;

import com.example.starter.common.response.ApiResponse;
import com.example.starter.domain.${domain}.adapter.in.web.dto.${Name}Request;
import com.example.starter.domain.${domain}.adapter.in.web.dto.${Name}Response;
import com.example.starter.domain.${domain}.application.port.in.Create${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Get${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Update${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Delete${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.command.Create${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.Update${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ${Name} 컨트롤러 (입력 어댑터)
 */
@RestController
@RequestMapping("/api/v1/${domain}s")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "${Name} API", description = "${Name} CRUD 관련 API")
public class ${Name}Controller {

  private final Create${Name}UseCase create${Name}UseCase;
  private final Get${Name}UseCase get${Name}UseCase;
  private final Update${Name}UseCase update${Name}UseCase;
  private final Delete${Name}UseCase delete${Name}UseCase;

  /**
   * ${Name} 생성
   */
  @PostMapping
  @Operation(summary = "${Name} 생성", description = "새로운 ${name}을 생성합니다")
  public ResponseEntity<ApiResponse<${Name}Response>> create${Name}(
      @Valid @RequestBody ${Name}Request request,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());

    Create${Name}Command command = new Create${Name}Command(
      ${fields.request_to_command}
    );

    ${Name}Result result = create${Name}UseCase.create${Name}(userId, command);
    ${Name}Response response = ${Name}Response.from${Name}Result(result);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "${Name}이 생성되었습니다"));
  }

  /**
   * ${Name} 상세 조회
   */
  @GetMapping("/{id}")
  @Operation(summary = "${Name} 상세 조회", description = "${Name} ID로 ${name}을 조회합니다")
  public ResponseEntity<ApiResponse<${Name}Response>> get${Name}ById(
      @PathVariable Long id,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());

    ${Name}Result result = get${Name}UseCase.get${Name}ById(id, userId);
    ${Name}Response response = ${Name}Response.from${Name}Result(result);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * 내 ${Name} 목록 조회
   */
  @GetMapping
  @Operation(summary = "내 ${Name} 목록 조회", description = "내 ${Name}을 페이징하여 조회합니다")
  public ResponseEntity<ApiResponse<Page<${Name}Response>>> getAll${Name}s(
      Pageable pageable,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());

    Page<${Name}Result> resultPage = get${Name}UseCase.getAll${Name}s(userId, pageable);
    Page<${Name}Response> response = resultPage.map(${Name}Response::from${Name}Result);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * ${Name} 수정
   */
  @PutMapping("/{id}")
  @Operation(summary = "${Name} 수정", description = "${Name}을 수정합니다")
  public ResponseEntity<ApiResponse<${Name}Response>> update${Name}(
      @PathVariable Long id,
      @Valid @RequestBody ${Name}Request request,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());

    Update${Name}Command command = new Update${Name}Command(
      ${fields.request_to_command}
    );

    ${Name}Result result = update${Name}UseCase.update${Name}(id, userId, command);
    ${Name}Response response = ${Name}Response.from${Name}Result(result);

    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "${Name}이 수정되었습니다"));
  }

  /**
   * ${Name} 삭제
   */
  @DeleteMapping("/{id}")
  @Operation(summary = "${Name} 삭제", description = "${Name}을 삭제합니다")
  public ResponseEntity<ApiResponse<Void>> delete${Name}(
      @PathVariable Long id,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());

    delete${Name}UseCase.delete${Name}(id, userId);

    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(ApiResponse.success(null, "${Name}이 삭제되었습니다"));
  }
}
```

---

## 9. Entity

### ${Name}.java

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/domain/${Name}.java`

```java
package com.example.starter.domain.${domain}.domain;

import com.example.starter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * ${Name} 엔티티
 */
@Entity
@Table(name = "${domain}s")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ${Name} extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private Long userId;

  ${fields.entity_columns}

  @Builder.Default
  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ${Name}Status status = ${Name}Status.${DEFAULT_STATUS};

  // 비즈니스 메서드

  /**
   * 상태를 ${DEFAULT_STATUS}으로 변경합니다
   */
  public void ${default_status_method}() {
    this.status = ${Name}Status.${DEFAULT_STATUS};
  }

  ${fields.entity_methods}
}
```

### ${Name}Status.java (필요시)

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/domain/${Name}Status.java`

```java
package com.example.starter.domain.${domain}.domain;

/**
 * ${Name} 상태 열거형
 */
public enum ${Name}Status {
  ${status_values}  // PENDING, COMPLETED 등
}
```

---

## 10. JPA Repository

### ${Name}JpaRepository

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/adapter/out/persistence/${Name}JpaRepository.java`

```java
package com.example.starter.domain.${domain}.adapter.out.persistence;

import com.example.starter.domain.${domain}.domain.${Name};
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * ${Name} JPA 저장소
 */
@Repository
public interface ${Name}JpaRepository extends JpaRepository<${Name}, Long> {

  /**
   * userId와 ID로 ${name}을 조회합니다
   */
  Optional<${Name}> findByIdAndUserId(Long id, Long userId);

  /**
   * userId로 모든 ${name}을 조회합니다
   */
  Page<${Name}> findByUserId(Long userId, Pageable pageable);
}
```

---

## 11. Persistence Adapter

### ${Name}PersistenceAdapter

**파일 경로**: `src/main/java/com/example/starter/domain/${domain}/adapter/out/persistence/${Name}PersistenceAdapter.java`

```java
package com.example.starter.domain.${domain}.adapter.out.persistence;

import com.example.starter.domain.${domain}.application.port.out.${Name}Port;
import com.example.starter.domain.${domain}.domain.${Name};
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * ${Name} 영속성 어댑터 (Out Port 구현)
 */
@Component
@RequiredArgsConstructor
public class ${Name}PersistenceAdapter implements ${Name}Port {

  private final ${Name}JpaRepository ${domain}JpaRepository;

  @Override
  public ${Name} save(${Name} entity) {
    return ${domain}JpaRepository.save(entity);
  }

  @Override
  public Optional<${Name}> findByIdAndUserId(Long id, Long userId) {
    return ${domain}JpaRepository.findByIdAndUserId(id, userId);
  }

  @Override
  public Page<${Name}> findByUserId(Long userId, Pageable pageable) {
    return ${domain}JpaRepository.findByUserId(userId, pageable);
  }

  @Override
  public void delete(${Name} entity) {
    ${domain}JpaRepository.delete(entity);
  }

  @Override
  public Optional<${Name}> findById(Long id) {
    return ${domain}JpaRepository.findById(id);
  }
}
```

---

## 12. Flyway 마이그레이션

### V${NEXT}__create_${domain}s_table.sql

**파일 경로**: `src/main/resources/db/migration/V${NEXT}__create_${domain}s_table.sql`

```sql
-- V3__create_products_table.sql
CREATE TABLE ${domain}s (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  ${fields.sql_columns}
  status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_${domain}s_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE INDEX idx_${domain}s_user_id ON ${domain}s(user_id);
```

**필드 SQL 생성 규칙**:
- String → `VARCHAR(255) NOT NULL`
- Long → `BIGINT NOT NULL`
- Integer → `INT NOT NULL`
- Double → `DECIMAL(19, 2) NOT NULL`
- Boolean → `BOOLEAN NOT NULL DEFAULT false`
- LocalDate → `DATE NOT NULL`
- LocalDateTime → `TIMESTAMP NOT NULL`

---

## 13. ErrorCode 업데이트

**파일**: `src/main/java/com/example/starter/common/exception/ErrorCode.java`

추가 항목:

```java
${DOMAIN}_NOT_FOUND(HttpStatus.NOT_FOUND, "${Name}을 찾을 수 없습니다"),
${DOMAIN}_ALREADY_EXISTS(HttpStatus.CONFLICT, "${Name}이 이미 존재합니다"),
```

**예시**:
```java
PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다"),
PRODUCT_ALREADY_EXISTS(HttpStatus.CONFLICT, "상품이 이미 존재합니다"),
```
