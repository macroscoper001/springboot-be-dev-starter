package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.application.port.in.command.UpdateTodoCommand;

/**
 * 할일 수정 유스케이스
 */
public interface UpdateTodoUseCase {

  /**
   * 할일 수정
   */
  TodoResult updateTodo(Long todoId, Long userId, UpdateTodoCommand command);
}
