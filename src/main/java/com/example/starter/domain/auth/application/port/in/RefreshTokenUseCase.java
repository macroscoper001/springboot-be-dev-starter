package com.example.starter.domain.auth.application.port.in;

import com.example.starter.domain.auth.adapter.in.web.dto.LoginResponse;
import com.example.starter.domain.auth.adapter.in.web.dto.RefreshTokenRequest;

/**
 * 토큰 갱신 유스케이스
 */
public interface RefreshTokenUseCase {

  /**
   * 토큰 갱신
   */
  LoginResponse refreshToken(RefreshTokenRequest request);
}
