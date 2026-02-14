package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 할일 조회 유스케이스
 */
public interface GetTodoUseCase {

  /**
   * 할일 조회
   */
  TodoResponse getTodoById(Long todoId, Long userId);

  /**
   * 사용자의 할일 목록 조회
   */
  Page<TodoResponse> getUserTodos(Long userId, Pageable pageable);
}
