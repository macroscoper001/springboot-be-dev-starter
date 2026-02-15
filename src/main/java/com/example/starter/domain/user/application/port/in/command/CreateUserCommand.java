package com.example.starter.domain.user.application.port.in.command;

/**
 * 사용자 생성 커맨드
 */
public record CreateUserCommand(
  String email,
  String username,
  String password,
  String name
) {}
