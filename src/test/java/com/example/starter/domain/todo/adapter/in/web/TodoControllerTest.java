package com.example.starter.domain.todo.adapter.in.web;

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.application.port.in.ChangeTodoStatusUseCase;
import com.example.starter.domain.todo.application.port.in.CreateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.DeleteTodoUseCase;
import com.example.starter.domain.todo.application.port.in.GetTodoUseCase;
import com.example.starter.domain.todo.application.port.in.UpdateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.command.CreateTodoCommand;
import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.domain.TodoStatus;
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
@DisplayName("할일 컨트롤러 테스트")
class TodoControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @MockBean
  private CreateTodoUseCase createTodoUseCase;

  @MockBean
  private GetTodoUseCase getTodoUseCase;

  @MockBean
  private UpdateTodoUseCase updateTodoUseCase;

  @MockBean
  private DeleteTodoUseCase deleteTodoUseCase;

  @MockBean
  private ChangeTodoStatusUseCase changeTodoStatusUseCase;

  private TodoResult testTodoResult;

  @BeforeEach
  void setUp() {
    testTodoResult = new TodoResult(
        1L,
        1L,
        "테스트 할일",
        "테스트 설명",
        TodoStatus.PENDING,
        LocalDateTime.now(),
        LocalDateTime.now()
    );
  }

  @Test
  @WithMockUser(username = "1")
  @DisplayName("할일 생성 - 성공")
  void testCreateTodo_Success() throws Exception {
    // Given
    TodoRequest request = TodoRequest.builder()
        .title("테스트 할일")
        .description("테스트 설명")
        .build();

    when(createTodoUseCase.createTodo(anyLong(), any(CreateTodoCommand.class)))
        .thenReturn(testTodoResult);

    // When & Then
    mockMvc.perform(post("/api/v1/todos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success", notNullValue()));

    verify(createTodoUseCase, times(1)).createTodo(anyLong(), any(CreateTodoCommand.class));
  }

  @Test
  @WithMockUser(username = "1")
  @DisplayName("할일 조회 - 성공")
  void testGetTodoById_Success() throws Exception {
    // Given
    when(getTodoUseCase.getTodoById(anyLong(), anyLong()))
        .thenReturn(testTodoResult);

    // When & Then
    mockMvc.perform(get("/api/v1/todos/1")
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success", notNullValue()));

    verify(getTodoUseCase, times(1)).getTodoById(anyLong(), anyLong());
  }
}
