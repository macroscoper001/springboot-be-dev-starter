package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;

/**
 * 할일 생성 유스케이스
 */
public interface CreateTodoUseCase {

  /**
   * 할일 생성
   */
  TodoResponse createTodo(Long userId, TodoRequest request);
}
