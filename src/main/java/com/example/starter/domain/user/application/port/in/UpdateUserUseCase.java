package com.example.starter.domain.user.application.port.in;

import com.example.starter.domain.user.application.port.in.command.UpdateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UserResult;

/**
 * 사용자 수정 유스케이스
 */
public interface UpdateUserUseCase {

  /**
   * 사용자 정보 수정
   */
  UserResult updateUser(Long userId, UpdateUserCommand command);
}
