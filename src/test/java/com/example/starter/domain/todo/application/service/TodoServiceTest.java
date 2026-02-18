package com.example.starter.domain.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.domain.todo.application.port.in.command.CreateTodoCommand;
import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.application.port.in.command.UpdateTodoCommand;
import com.example.starter.domain.todo.application.port.out.TodoPort;
import com.example.starter.domain.todo.domain.Todo;
import com.example.starter.domain.todo.domain.TodoStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
@DisplayName("할일 서비스 테스트")
@SuppressWarnings("null")
class TodoServiceTest {

  @Mock
  private TodoPort todoPort;

  @InjectMocks
  private TodoService todoService;

  private CreateTodoCommand validCommand;
  private Todo testTodo;

  @BeforeEach
  void setUp() {
    validCommand = new CreateTodoCommand(
        "테스트 할일",
        "테스트 설명"
    );

    testTodo = Todo.builder()
        .id(1L)
        .title("테스트 할일")
        .description("테스트 설명")
        .status(TodoStatus.PENDING)
        .userId(1L)
        .build();
  }

  @Test
  @DisplayName("할일 생성 - 성공")
  void testCreateTodo_Success() {
    // Given
    Long userId = 1L;
    when(todoPort.save(any(Todo.class))).thenReturn(testTodo);

    // When
    TodoResult result = todoService.createTodo(userId, validCommand);

    // Then
    assertNotNull(result);
    assertEquals("테스트 할일", result.title());
    assertEquals(TodoStatus.PENDING, result.status());
    verify(todoPort, times(1)).save(any(Todo.class));
  }

  @Test
  @DisplayName("할일 조회 - 성공")
  void testGetTodoById_Success() {
    // Given
    Long userId = 1L;
    when(todoPort.findByIdAndUserId(1L, userId))
        .thenReturn(java.util.Optional.of(testTodo));

    // When
    TodoResult result = todoService.getTodoById(1L, userId);

    // Then
    assertNotNull(result);
    assertEquals("테스트 할일", result.title());
  }

  @Test
  @DisplayName("할일 조회 - 할일 없음")
  void testGetTodoById_NotFound() {
    // Given
    Long userId = 1L;
    when(todoPort.findByIdAndUserId(999L, userId))
        .thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BusinessException.class, () -> todoService.getTodoById(999L, userId));
  }

  @Test
  @DisplayName("사용자의 할일 목록 조회 - 성공")
  void testGetUserTodos_Success() {
    // Given
    Long userId = 1L;
    Pageable pageable = PageRequest.of(0, 10);
    Page<Todo> todoPage = new PageImpl<>(java.util.List.of(testTodo));

    when(todoPort.findByUserId(userId, pageable)).thenReturn(todoPage);

    // When
    Page<TodoResult> result = todoService.getUserTodos(userId, pageable);

    // Then
    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  @DisplayName("할일 수정 - 성공")
  void testUpdateTodo_Success() {
    // Given
    Long userId = 1L;
    UpdateTodoCommand updateCommand = new UpdateTodoCommand("수정된 제목", "수정된 설명");

    when(todoPort.findByIdAndUserId(1L, userId))
        .thenReturn(java.util.Optional.of(testTodo));
    when(todoPort.save(any(Todo.class))).thenReturn(testTodo);

    // When
    TodoResult result = todoService.updateTodo(1L, userId, updateCommand);

    // Then
    assertNotNull(result);
    verify(todoPort, times(1)).save(any(Todo.class));
  }

  @Test
  @DisplayName("할일 삭제 - 성공")
  void testDeleteTodo_Success() {
    // Given
    Long userId = 1L;
    when(todoPort.findByIdAndUserId(1L, userId))
        .thenReturn(java.util.Optional.of(testTodo));

    // When
    assertDoesNotThrow(() -> todoService.deleteTodo(1L, userId));

    // Then
    verify(todoPort, times(1)).delete(any(Todo.class));
  }

  @Test
  @DisplayName("할일 완료 처리 - 성공")
  void testCompleteTodo_Success() {
    // Given
    Long userId = 1L;
    when(todoPort.findByIdAndUserId(1L, userId))
        .thenReturn(java.util.Optional.of(testTodo));
    when(todoPort.save(any(Todo.class))).thenReturn(testTodo);

    // When
    TodoResult result = todoService.completeTodo(1L, userId);

    // Then
    assertNotNull(result);
    assertEquals(TodoStatus.COMPLETED, result.status());
    verify(todoPort, times(1)).save(any(Todo.class));
  }
}
