package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;

/**
 * 할일 수정 유스케이스
 */
public interface UpdateTodoUseCase {

  /**
   * 할일 수정
   */
  TodoResponse updateTodo(Long todoId, Long userId, TodoRequest request);
}
