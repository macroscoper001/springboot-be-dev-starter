package com.example.starter.domain.auth.application.port.in.command;

/**
 * 로그인 커맨드
 */
public record LoginCommand(
  String username,
  String password
) {}
