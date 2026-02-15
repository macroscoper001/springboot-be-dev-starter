package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 할일 조회 유스케이스
 */
public interface GetTodoUseCase {

  /**
   * 할일 조회
   */
  TodoResult getTodoById(Long todoId, Long userId);

  /**
   * 사용자의 할일 목록 조회
   */
  Page<TodoResult> getUserTodos(Long userId, Pageable pageable);
}
