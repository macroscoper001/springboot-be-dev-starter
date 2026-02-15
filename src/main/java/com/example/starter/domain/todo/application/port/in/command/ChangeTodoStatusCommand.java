package com.example.starter.domain.todo.application.port.in.command;

import com.example.starter.domain.todo.domain.TodoStatus;

/**
 * 할일 상태 변경 커맨드
 */
public record ChangeTodoStatusCommand(
  TodoStatus status
) {}
