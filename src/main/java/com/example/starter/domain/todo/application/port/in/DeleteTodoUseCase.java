package com.example.starter.domain.todo.application.port.in;

/**
 * 할일 삭제 유스케이스
 */
public interface DeleteTodoUseCase {

  /**
   * 할일 삭제
   */
  void deleteTodo(Long todoId, Long userId);
}
