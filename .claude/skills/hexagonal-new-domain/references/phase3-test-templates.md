# Phase 3: 테스트 파일 템플릿

## Service 테스트

### ${Name}ServiceTest

**파일 경로**: `src/test/java/com/example/starter/domain/${domain}/application/service/${Name}ServiceTest.java`

```java
package com.example.starter.domain.${domain}.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.${domain}.application.port.in.command.Create${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.Update${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import com.example.starter.domain.${domain}.application.port.out.${Name}Port;
import com.example.starter.domain.${domain}.domain.${Name};
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * ${Name} 서비스 테스트
 */
@DisplayName("${Name} 서비스")
@ExtendWith(MockitoExtension.class)
class ${Name}ServiceTest {

  @Mock
  private ${Name}Port ${domain}Port;

  @InjectMocks
  private ${Name}Service ${domain}Service;

  @Test
  @DisplayName("새 ${name}을 생성할 수 있다")
  void testCreate${Name}_Success() {
    // Given
    Long userId = 1L;
    Create${Name}Command command = new Create${Name}Command(
      ${fields.test_command}
    );

    ${Name} entity = ${Name}.builder()
        ${fields.test_builder}
        .userId(userId)
        .id(1L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(${domain}Port.save(any(${Name}.class))).thenReturn(entity);

    // When
    ${Name}Result result = ${domain}Service.create${Name}(userId, command);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals(userId, result.userId());
    verify(${domain}Port, times(1)).save(any(${Name}.class));
  }

  @Test
  @DisplayName("존재하지 않는 ${name}을 조회하면 예외가 발생한다")
  void testGet${Name}ById_NotFound() {
    // Given
    when(${domain}Port.findByIdAndUserId(1L, 1L)).thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BusinessException.class, () ->
        ${domain}Service.get${Name}ById(1L, 1L)
    );
    verify(${domain}Port, times(1)).findByIdAndUserId(1L, 1L);
  }

  @Test
  @DisplayName("${name}을 조회할 수 있다")
  void testGet${Name}ById_Success() {
    // Given
    Long userId = 1L;
    Long id = 1L;
    ${Name} entity = ${Name}.builder()
        ${fields.test_builder}
        .userId(userId)
        .id(id)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(${domain}Port.findByIdAndUserId(id, userId)).thenReturn(java.util.Optional.of(entity));

    // When
    ${Name}Result result = ${domain}Service.get${Name}ById(id, userId);

    // Then
    assertNotNull(result);
    assertEquals(id, result.id());
    assertEquals(userId, result.userId());
    verify(${domain}Port, times(1)).findByIdAndUserId(id, userId);
  }

  @Test
  @DisplayName("${name}을 수정할 수 있다")
  void testUpdate${Name}_Success() {
    // Given
    Long userId = 1L;
    Long id = 1L;
    Update${Name}Command command = new Update${Name}Command(
      ${fields.test_command}
    );

    ${Name} entity = ${Name}.builder()
        ${fields.test_builder}
        .userId(userId)
        .id(id)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(${domain}Port.findByIdAndUserId(id, userId)).thenReturn(java.util.Optional.of(entity));
    when(${domain}Port.save(any(${Name}.class))).thenReturn(entity);

    // When
    ${Name}Result result = ${domain}Service.update${Name}(id, userId, command);

    // Then
    assertNotNull(result);
    assertEquals(id, result.id());
    verify(${domain}Port, times(1)).findByIdAndUserId(id, userId);
    verify(${domain}Port, times(1)).save(any(${Name}.class));
  }

  @Test
  @DisplayName("존재하지 않는 ${name}을 수정하면 예외가 발생한다")
  void testUpdate${Name}_NotFound() {
    // Given
    Long userId = 1L;
    Long id = 1L;
    Update${Name}Command command = new Update${Name}Command(
      ${fields.test_command}
    );

    when(${domain}Port.findByIdAndUserId(id, userId)).thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BusinessException.class, () ->
        ${domain}Service.update${Name}(id, userId, command)
    );
    verify(${domain}Port, times(1)).findByIdAndUserId(id, userId);
    verify(${domain}Port, never()).save(any());
  }

  @Test
  @DisplayName("${name}을 삭제할 수 있다")
  void testDelete${Name}_Success() {
    // Given
    Long userId = 1L;
    Long id = 1L;
    ${Name} entity = ${Name}.builder()
        ${fields.test_builder}
        .userId(userId)
        .id(id)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();

    when(${domain}Port.findByIdAndUserId(id, userId)).thenReturn(java.util.Optional.of(entity));

    // When
    ${domain}Service.delete${Name}(id, userId);

    // Then
    verify(${domain}Port, times(1)).findByIdAndUserId(id, userId);
    verify(${domain}Port, times(1)).delete(entity);
  }

  @Test
  @DisplayName("존재하지 않는 ${name}을 삭제하면 예외가 발생한다")
  void testDelete${Name}_NotFound() {
    // Given
    Long userId = 1L;
    Long id = 1L;

    when(${domain}Port.findByIdAndUserId(id, userId)).thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BusinessException.class, () ->
        ${domain}Service.delete${Name}(id, userId)
    );
    verify(${domain}Port, times(1)).findByIdAndUserId(id, userId);
    verify(${domain}Port, never()).delete(any());
  }
}
```

---

## Controller 테스트

### ${Name}ControllerTest

**파일 경로**: `src/test/java/com/example/starter/domain/${domain}/adapter/in/web/${Name}ControllerTest.java`

```java
package com.example.starter.domain.${domain}.adapter.in.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.starter.domain.${domain}.adapter.in.web.dto.${Name}Request;
import com.example.starter.domain.${domain}.application.port.in.Create${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Get${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Update${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.Delete${Name}UseCase;
import com.example.starter.domain.${domain}.application.port.in.command.Create${Name}Command;
import com.example.starter.domain.${domain}.application.port.in.command.${Name}Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

/**
 * ${Name} 컨트롤러 테스트
 */
@DisplayName("${Name} 컨트롤러")
@SpringBootTest
@AutoConfigureMockMvc
class ${Name}ControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private Create${Name}UseCase create${Name}UseCase;

  @MockBean
  private Get${Name}UseCase get${Name}UseCase;

  @MockBean
  private Update${Name}UseCase update${Name}UseCase;

  @MockBean
  private Delete${Name}UseCase delete${Name}UseCase;

  @Test
  @DisplayName("새 ${name}을 생성할 수 있다")
  @WithMockUser(username = "1")
  void testCreate${Name}_Success() throws Exception {
    // Given
    ${Name}Request request = new ${Name}Request(
      ${fields.test_request}
    );

    ${Name}Result result = new ${Name}Result(
      1L,
      1L,
      ${fields.test_result},
      LocalDateTime.now(),
      LocalDateTime.now()
    );

    when(create${Name}UseCase.create${Name}(any(), any())).thenReturn(result);

    // When & Then
    mockMvc.perform(post("/api/v1/${domain}s")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.data.id").value(1L));
  }

  @Test
  @DisplayName("${name}을 조회할 수 있다")
  @WithMockUser(username = "1")
  void testGet${Name}ById_Success() throws Exception {
    // Given
    Long id = 1L;
    ${Name}Result result = new ${Name}Result(
      id,
      1L,
      ${fields.test_result},
      LocalDateTime.now(),
      LocalDateTime.now()
    );

    when(get${Name}UseCase.get${Name}ById(id, 1L)).thenReturn(result);

    // When & Then
    mockMvc.perform(get("/api/v1/${domain}s/" + id))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.id").value(id));
  }

  @Test
  @DisplayName("${name} 목록을 조회할 수 있다")
  @WithMockUser(username = "1")
  void testGetAll${Name}s_Success() throws Exception {
    // When & Then
    mockMvc.perform(get("/api/v1/${domain}s"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data").isArray());
  }

  @Test
  @DisplayName("${name}을 수정할 수 있다")
  @WithMockUser(username = "1")
  void testUpdate${Name}_Success() throws Exception {
    // Given
    Long id = 1L;
    ${Name}Request request = new ${Name}Request(
      ${fields.test_request}
    );

    ${Name}Result result = new ${Name}Result(
      id,
      1L,
      ${fields.test_result},
      LocalDateTime.now(),
      LocalDateTime.now()
    );

    when(update${Name}UseCase.update${Name}(any(), any(), any())).thenReturn(result);

    // When & Then
    mockMvc.perform(put("/api/v1/${domain}s/" + id)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.data.id").value(id));
  }

  @Test
  @DisplayName("${name}을 삭제할 수 있다")
  @WithMockUser(username = "1")
  void testDelete${Name}_Success() throws Exception {
    // Given
    Long id = 1L;

    // When & Then
    mockMvc.perform(delete("/api/v1/${domain}s/" + id))
      .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("인증이 없으면 401 에러가 발생한다")
  void testCreate${Name}_Unauthorized() throws Exception {
    // Given
    ${Name}Request request = new ${Name}Request(
      ${fields.test_request}
    );

    // When & Then
    mockMvc.perform(post("/api/v1/${domain}s")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(request)))
      .andExpect(status().isUnauthorized());
  }
}
```

---

## 테스트 작성 팁

### Service 테스트 특징
- **Mock**: Out Port를 Mock으로 주입
- **입력**: Command 객체
- **검증**: Result 객체의 필드 값
- **예외 테스트**: BusinessException 발생 확인

### Controller 테스트 특징
- **Mock**: UseCase를 Mock으로 주입
- **입력**: HTTP Request (Request DTO)
- **검증**: HTTP 상태 코드 및 응답 JSON
- **인증**: `@WithMockUser(username = "1")` 어노테이션

### 테스트 명명 규칙
- `test${Method}_Success` - 성공 케이스
- `test${Method}_NotFound` - 리소스 없음
- `test${Method}_Unauthorized` - 인증 실패
- `test${Method}_InvalidInput` - 입력 검증 실패
