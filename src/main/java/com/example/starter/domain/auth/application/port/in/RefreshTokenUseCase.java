package com.example.starter.domain.auth.application.port.in;

import com.example.starter.domain.auth.application.port.in.command.AuthResult;
import com.example.starter.domain.auth.application.port.in.command.RefreshTokenCommand;

/**
 * 토큰 갱신 유스케이스
 */
public interface RefreshTokenUseCase {

  /**
   * 토큰 갱신
   */
  AuthResult refreshToken(RefreshTokenCommand command);
}
