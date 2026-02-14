package com.example.starter.domain.todo.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;
import com.example.starter.domain.todo.application.port.in.ChangeTodoStatusUseCase;
import com.example.starter.domain.todo.application.port.in.CreateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.DeleteTodoUseCase;
import com.example.starter.domain.todo.application.port.in.GetTodoUseCase;
import com.example.starter.domain.todo.application.port.in.UpdateTodoUseCase;
import com.example.starter.domain.todo.application.port.out.TodoPort;
import com.example.starter.domain.todo.domain.Todo;
import com.example.starter.domain.todo.domain.TodoStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 할일 애플리케이션 서비스 (UseCase 구현체)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class TodoService implements CreateTodoUseCase, GetTodoUseCase, UpdateTodoUseCase, DeleteTodoUseCase, ChangeTodoStatusUseCase {

  private final TodoPort todoPort;

  @Override
  public TodoResponse createTodo(Long userId, TodoRequest request) {
    Todo todo = Todo.builder()
        .title(request.getTitle())
        .description(request.getDescription())
        .status(TodoStatus.PENDING)
        .userId(userId)
        .build();

    Todo savedTodo = todoPort.save(todo);
    log.info("할일 생성 완료: todoId={}, userId={}", savedTodo.getId(), userId);

    return TodoResponse.fromEntity(savedTodo);
  }

  @Override
  @Transactional(readOnly = true)
  public TodoResponse getTodoById(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    return TodoResponse.fromEntity(todo);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TodoResponse> getUserTodos(Long userId, Pageable pageable) {
    return todoPort.findByUserId(userId, pageable)
        .map(TodoResponse::fromEntity);
  }

  @Override
  public TodoResponse updateTodo(Long todoId, Long userId, TodoRequest request) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.updateTitle(request.getTitle());
    todo.updateDescription(request.getDescription());

    Todo updatedTodo = todoPort.save(todo);
    log.info("할일 수정 완료: todoId={}, userId={}", todoId, userId);

    return TodoResponse.fromEntity(updatedTodo);
  }

  @Override
  public void deleteTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todoPort.delete(todo);
    log.info("할일 삭제 완료: todoId={}, userId={}", todoId, userId);
  }

  @Override
  public TodoResponse completeTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.complete();
    Todo updatedTodo = todoPort.save(todo);

    log.info("할일 완료: todoId={}, userId={}", todoId, userId);

    return TodoResponse.fromEntity(updatedTodo);
  }

  @Override
  public TodoResponse pendingTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.pending();
    Todo updatedTodo = todoPort.save(todo);

    log.info("할일 미완료 처리: todoId={}, userId={}", todoId, userId);

    return TodoResponse.fromEntity(updatedTodo);
  }
}
