package com.example.starter.domain.user.application.port.in.command;

import java.time.LocalDateTime;

/**
 * 사용자 응답 결과 (애플리케이션 계층 전용)
 */
public record UserResult(
  Long id,
  String email,
  String username,
  String name,
  boolean active,
  LocalDateTime createdAt,
  LocalDateTime updatedAt
) {}
