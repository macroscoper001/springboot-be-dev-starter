package com.example.starter.domain.user.application.port.in;

import com.example.starter.domain.user.application.port.in.command.CreateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UserResult;

/**
 * 사용자 생성 유스케이스
 */
public interface CreateUserUseCase {

  /**
   * 사용자 생성
   */
  UserResult createUser(CreateUserCommand command);
}
