package com.example.starter.domain.todo.application.port.in;

import com.example.starter.domain.todo.application.port.in.command.TodoResult;

/**
 * 할일 상태 변경 유스케이스
 */
public interface ChangeTodoStatusUseCase {

  /**
   * 할일 완료 처리
   */
  TodoResult completeTodo(Long todoId, Long userId);

  /**
   * 할일 미완료 처리
   */
  TodoResult pendingTodo(Long todoId, Long userId);
}
