package com.example.starter.domain.todo.application.port.in.command;

import com.example.starter.domain.todo.domain.TodoStatus;
import java.time.LocalDateTime;

/**
 * 할일 응답 결과 (애플리케이션 계층 전용)
 */
public record TodoResult(
  Long id,
  Long userId,
  String title,
  String description,
  TodoStatus status,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
