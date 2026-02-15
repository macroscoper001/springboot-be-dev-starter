package com.example.starter.domain.todo.application.port.in.command;

/**
 * 할일 생성 커맨드
 */
public record CreateTodoCommand(
  String title,
  String description
) {}
