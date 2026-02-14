package com.example.starter.domain.user.application.port.in;

/**
 * 사용자 삭제 유스케이스
 */
public interface DeleteUserUseCase {

  /**
   * 사용자 삭제
   */
  void deleteUser(Long userId);
}
