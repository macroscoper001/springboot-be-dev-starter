package com.example.starter.domain.auth.application.port.in.command;

/**
 * 토큰 갱신 커맨드
 */
public record RefreshTokenCommand(
  String refreshToken
) {}
