package com.example.starter.domain.auth.application.port.in.command;

/**
 * 인증 응답 결과 (애플리케이션 계층 전용)
 */
public record AuthResult(
  String accessToken,
  String refreshToken,
  long expiresIn
) {}
