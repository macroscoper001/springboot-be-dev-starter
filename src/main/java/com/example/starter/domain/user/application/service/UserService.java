package com.example.starter.domain.user.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.user.application.port.in.CreateUserUseCase;
import com.example.starter.domain.user.application.port.in.DeleteUserUseCase;
import com.example.starter.domain.user.application.port.in.GetUserUseCase;
import com.example.starter.domain.user.application.port.in.UpdateUserUseCase;
import com.example.starter.domain.user.application.port.in.command.CreateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UpdateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UserResult;
import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 애플리케이션 서비스 (UseCase 구현체)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserService implements CreateUserUseCase, GetUserUseCase, UpdateUserUseCase, DeleteUserUseCase {

  private final UserPort userPort;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserResult createUser(CreateUserCommand command) {
    // 이메일 중복 확인
    if (userPort.existsByEmail(command.email())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다");
    }

    // 사용자명 중복 확인
    if (userPort.existsByUsername(command.username())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 사용자명입니다");
    }

    User user = User.builder()
        .email(command.email())
        .username(command.username())
        .password(passwordEncoder.encode(command.password()))
        .name(command.name())
        .build();

    User savedUser = userPort.save(user);
    log.info("사용자 생성 완료: userId={}", savedUser.getId());

    return toUserResult(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResult getUserById(Long userId) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return toUserResult(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResult> getAllUsers(Pageable pageable) {
    return userPort.findAll(pageable)
        .map(this::toUserResult);
  }

  @Override
  public UserResult updateUser(Long userId, UpdateUserCommand command) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 다른 사용자의 이메일로 변경 시도 확인
    if (!user.getEmail().equals(command.email()) && userPort.existsByEmail(command.email())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다");
    }

    // 다른 사용자의 사용자명으로 변경 시도 확인
    if (!user.getUsername().equals(command.username()) && userPort.existsByUsername(command.username())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 사용자명입니다");
    }

    // 도메인 메서드를 통한 사용자 정보 업데이트
    user.updateProfile(
      command.email(),
      command.username(),
      passwordEncoder.encode(command.password()),
      command.name()
    );

    User updatedUser = userPort.save(user);
    log.info("사용자 정보 업데이트 완료: userId={}", updatedUser.getId());

    return toUserResult(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    userPort.delete(user);
    log.info("사용자 삭제 완료: userId={}", userId);
  }

  /**
   * User 엔티티를 UserResult로 변환
   */
  private UserResult toUserResult(User user) {
    return new UserResult(
      user.getId(),
      user.getEmail(),
      user.getUsername(),
      user.getName(),
      user.isActive(),
      user.getCreatedAt(),
      user.getUpdatedAt()
    );
  }
}
