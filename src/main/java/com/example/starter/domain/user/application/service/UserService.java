package com.example.starter.domain.user.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.adapter.in.web.dto.UserResponse;
import com.example.starter.domain.user.application.port.in.CreateUserUseCase;
import com.example.starter.domain.user.application.port.in.DeleteUserUseCase;
import com.example.starter.domain.user.application.port.in.GetUserUseCase;
import com.example.starter.domain.user.application.port.in.UpdateUserUseCase;
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
  public UserResponse createUser(UserRequest request) {
    // 이메일 중복 확인
    if (userPort.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다");
    }

    // 사용자명 중복 확인
    if (userPort.existsByUsername(request.getUsername())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 사용자명입니다");
    }

    User user = User.builder()
        .email(request.getEmail())
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .build();

    User savedUser = userPort.save(user);
    log.info("사용자 생성 완료: userId={}", savedUser.getId());

    return UserResponse.fromEntity(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserResponse getUserById(Long userId) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    return UserResponse.fromEntity(user);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<UserResponse> getAllUsers(Pageable pageable) {
    return userPort.findAll(pageable)
        .map(UserResponse::fromEntity);
  }

  @Override
  public UserResponse updateUser(Long userId, UserRequest request) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 다른 사용자의 이메일로 변경 시도 확인
    if (!user.getEmail().equals(request.getEmail()) && userPort.existsByEmail(request.getEmail())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 이메일입니다");
    }

    // 다른 사용자의 사용자명으로 변경 시도 확인
    if (!user.getUsername().equals(request.getUsername()) && userPort.existsByUsername(request.getUsername())) {
      throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS, "이미 존재하는 사용자명입니다");
    }

    // 기존 엔티티 업데이트
    user = User.builder()
        .id(user.getId())
        .email(request.getEmail())
        .username(request.getUsername())
        .password(passwordEncoder.encode(request.getPassword()))
        .name(request.getName())
        .active(user.isActive())
        .build();

    User updatedUser = userPort.save(user);
    log.info("사용자 정보 업데이트 완료: userId={}", updatedUser.getId());

    return UserResponse.fromEntity(updatedUser);
  }

  @Override
  public void deleteUser(Long userId) {
    User user = userPort.findById(userId)
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    userPort.delete(user);
    log.info("사용자 삭제 완료: userId={}", userId);
  }
}
