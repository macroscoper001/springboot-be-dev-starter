package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.application.port.in.command.CreateTodoCommand;
import com.example.starter.domain.todo.application.port.in.command.TodoResult;

/**
 * 할일 생성 유스케이스
 */
public interface CreateTodoUseCase {

  /**
   * 할일 생성
   */
  TodoResult createTodo(Long userId, CreateTodoCommand command);
}
