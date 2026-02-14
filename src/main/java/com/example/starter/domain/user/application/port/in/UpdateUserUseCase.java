package com.example.starter.domain.user.application.port.in;

import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.adapter.in.web.dto.UserResponse;

/**
 * 사용자 수정 유스케이스
 */
public interface UpdateUserUseCase {

  /**
   * 사용자 정보 수정
   */
  UserResponse updateUser(Long userId, UserRequest request);
}
