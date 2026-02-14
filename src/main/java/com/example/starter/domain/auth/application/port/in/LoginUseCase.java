package com.example.starter.domain.auth.application.port.in;

import com.example.starter.domain.auth.adapter.in.web.dto.LoginRequest;
import com.example.starter.domain.auth.adapter.in.web.dto.LoginResponse;

/**
 * 로그인 유스케이스
 */
public interface LoginUseCase {

  /**
   * 로그인
   */
  LoginResponse login(LoginRequest request);
}
