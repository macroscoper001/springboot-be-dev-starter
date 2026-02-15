package com.example.starter.domain.user.application.port.in.command;

/**
 * 사용자 수정 커맨드
 */
public record UpdateUserCommand(
  String email,
  String username,
  String password,
  String name
) {}
