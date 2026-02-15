package com.example.starter.domain.auth.application.port.in;

import com.example.starter.domain.auth.application.port.in.command.AuthResult;
import com.example.starter.domain.auth.application.port.in.command.LoginCommand;

/**
 * 로그인 유스케이스
 */
public interface LoginUseCase {

  /**
   * 로그인
   */
  AuthResult login(LoginCommand command);
}
