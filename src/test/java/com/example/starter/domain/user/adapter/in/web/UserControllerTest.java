package com.example.starter.domain.user.adapter.in.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.application.port.in.CreateUserUseCase;
import com.example.starter.domain.user.application.port.in.DeleteUserUseCase;
import com.example.starter.domain.user.application.port.in.GetUserUseCase;
import com.example.starter.domain.user.application.port.in.UpdateUserUseCase;
import com.example.starter.domain.user.application.port.in.command.CreateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UserResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("사용자 컨트롤러 테스트")
@SuppressWarnings("null")
class UserControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CreateUserUseCase createUserUseCase;

  @MockBean
  private GetUserUseCase getUserUseCase;

  @MockBean
  private UpdateUserUseCase updateUserUseCase;

  @MockBean
  private DeleteUserUseCase deleteUserUseCase;

  private UserResult testUserResult;

  @BeforeEach
  void setUp() {
    testUserResult = new UserResult(
        1L,
        "test@example.com",
        "testuser",
        "테스트 사용자",
        true,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  @Test
  @WithMockUser(username = "1")
  @DisplayName("사용자 생성 - 성공")
  void testCreateUser_Success() throws Exception {
    // Given
    UserRequest request = UserRequest.builder()
        .email("test@example.com")
        .username("testuser")
        .password("password123")
        .name("테스트 사용자")
        .build();

    when(createUserUseCase.createUser(any(CreateUserCommand.class))).thenReturn(testUserResult);

    // When & Then
    mockMvc.perform(post("/api/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success", notNullValue()));

    verify(createUserUseCase, times(1)).createUser(any(CreateUserCommand.class));
  }

  @Test
  @WithMockUser(username = "1")
  @DisplayName("사용자 조회 - 성공")
  void testGetUserById_Success() throws Exception {
    // Given
    when(getUserUseCase.getUserById(1L)).thenReturn(testUserResult);

    // When & Then
    mockMvc.perform(get("/api/v1/users/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", notNullValue()));

    verify(getUserUseCase, times(1)).getUserById(1L);
  }
}
