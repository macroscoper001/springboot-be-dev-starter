package com.example.starter.domain.user.application.port.in;

import com.example.starter.domain.user.application.port.in.command.UserResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 조회 유스케이스
 */
public interface GetUserUseCase {

  /**
   * 사용자 ID로 조회
   */
  UserResult getUserById(Long userId);

  /**
   * 모든 사용자 조회
   */
  Page<UserResult> getAllUsers(Pageable pageable);
}
