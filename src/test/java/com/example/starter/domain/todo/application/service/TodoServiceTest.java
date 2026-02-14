package com.example.starter.domain.todo.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;
import com.example.starter.domain.todo.application.port.out.TodoPort;
import com.example.starter.domain.todo.domain.Todo;
import com.example.starter.domain.todo.domain.TodoStatus;
import java.time.LocalDateTime;
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
class TodoServiceTest {

  @Mock
  private TodoPort todoPort;

  @InjectMocks
  private TodoService todoService;

  private TodoRequest validRequest;
  private Todo testTodo;

  @BeforeEach
  void setUp() {
    validRequest = TodoRequest.builder()
        .title("테스트 할일")
        .description("테스트 설명")
        .build();

    testTodo = Todo.builder()
        .id(1L)
        .title("테스트 할일")
        .description("테스트 설명")
        .status(TodoStatus.PENDING)
        .userId(1L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .build();
  }

  @Test
  @DisplayName("할일 생성 - 성공")
  void testCreateTodo_Success() {
    // Given
    Long userId = 1L;
    when(todoPort.save(any(Todo.class))).thenReturn(testTodo);

    // When
    TodoResponse response = todoService.createTodo(userId, validRequest);

    // Then
    assertNotNull(response);
    assertEquals("테스트 할일", response.getTitle());
    assertEquals(TodoStatus.PENDING, response.getStatus());
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
    TodoResponse response = todoService.getTodoById(1L, userId);

    // Then
    assertNotNull(response);
    assertEquals("테스트 할일", response.getTitle());
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
    Page<TodoResponse> response = todoService.getUserTodos(userId, pageable);

    // Then
    assertNotNull(response);
    assertEquals(1, response.getTotalElements());
  }

  @Test
  @DisplayName("할일 수정 - 성공")
  void testUpdateTodo_Success() {
    // Given
    Long userId = 1L;
    TodoRequest updateRequest = TodoRequest.builder()
        .title("수정된 제목")
        .description("수정된 설명")
        .build();

    when(todoPort.findByIdAndUserId(1L, userId))
        .thenReturn(java.util.Optional.of(testTodo));
    when(todoPort.save(any(Todo.class))).thenReturn(testTodo);

    // When
    TodoResponse response = todoService.updateTodo(1L, userId, updateRequest);

    // Then
    assertNotNull(response);
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
    TodoResponse response = todoService.completeTodo(1L, userId);

    // Then
    assertNotNull(response);
    assertEquals(TodoStatus.PENDING, response.getStatus());
    verify(todoPort, times(1)).save(any(Todo.class));
  }
}
