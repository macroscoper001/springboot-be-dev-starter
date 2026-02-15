package com.example.starter.domain.todo.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.todo.application.port.in.ChangeTodoStatusUseCase;
import com.example.starter.domain.todo.application.port.in.CreateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.DeleteTodoUseCase;
import com.example.starter.domain.todo.application.port.in.GetTodoUseCase;
import com.example.starter.domain.todo.application.port.in.UpdateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.command.CreateTodoCommand;
import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.application.port.in.command.UpdateTodoCommand;
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
  public TodoResult createTodo(Long userId, CreateTodoCommand command) {
    Todo todo = Todo.builder()
        .title(command.title())
        .description(command.description())
        .userId(userId)
        .build();

    Todo savedTodo = todoPort.save(todo);
    log.info("할일 생성 완료: todoId={}, userId={}", savedTodo.getId(), userId);

    return toTodoResult(savedTodo);
  }

  @Override
  @Transactional(readOnly = true)
  public TodoResult getTodoById(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    return toTodoResult(todo);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<TodoResult> getUserTodos(Long userId, Pageable pageable) {
    return todoPort.findByUserId(userId, pageable)
        .map(this::toTodoResult);
  }

  @Override
  public TodoResult updateTodo(Long todoId, Long userId, UpdateTodoCommand command) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.updateTitle(command.title());
    todo.updateDescription(command.description());

    Todo updatedTodo = todoPort.save(todo);
    log.info("할일 수정 완료: todoId={}, userId={}", todoId, userId);

    return toTodoResult(updatedTodo);
  }

  @Override
  public void deleteTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todoPort.delete(todo);
    log.info("할일 삭제 완료: todoId={}, userId={}", todoId, userId);
  }

  @Override
  public TodoResult completeTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.complete();
    Todo updatedTodo = todoPort.save(todo);

    log.info("할일 완료: todoId={}, userId={}", todoId, userId);

    return toTodoResult(updatedTodo);
  }

  @Override
  public TodoResult pendingTodo(Long todoId, Long userId) {
    Todo todo = todoPort.findByIdAndUserId(todoId, userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND, "할일을 찾을 수 없습니다"));

    todo.pending();
    Todo updatedTodo = todoPort.save(todo);

    log.info("할일 미완료 처리: todoId={}, userId={}", todoId, userId);

    return toTodoResult(updatedTodo);
  }

  /**
   * Todo 엔티티를 TodoResult로 변환
   */
  private TodoResult toTodoResult(Todo todo) {
    return new TodoResult(
      todo.getId(),
      todo.getUserId(),
      todo.getTitle(),
      todo.getDescription(),
      todo.getStatus(),
      todo.getCreatedAt(),
      todo.getUpdatedAt()
    );
  }
}
