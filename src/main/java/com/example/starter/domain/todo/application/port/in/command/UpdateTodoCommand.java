package com.example.starter.domain.todo.application.port.in.command;

/**
 * 할일 수정 커맨드
 */
public record UpdateTodoCommand(
  String title,
  String description
) {}
