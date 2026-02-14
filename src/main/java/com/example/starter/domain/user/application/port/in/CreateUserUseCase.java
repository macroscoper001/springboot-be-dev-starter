package com.example.starter.domain.user.application.port.in;

import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.adapter.in.web.dto.UserResponse;

/**
 * 사용자 생성 유스케이스
 */
public interface CreateUserUseCase {

  /**
   * 사용자 생성
   */
  UserResponse createUser(UserRequest request);
}
