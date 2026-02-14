package com.example.starter.domain.auth.application.service;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.auth.adapter.in.web.dto.LoginRequest;
import com.example.starter.domain.auth.adapter.in.web.dto.LoginResponse;
import com.example.starter.domain.auth.adapter.in.web.dto.RefreshTokenRequest;
import com.example.starter.domain.auth.application.port.in.LoginUseCase;
import com.example.starter.domain.auth.application.port.in.RefreshTokenUseCase;
import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import com.example.starter.security.JwtProperties;
import com.example.starter.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 애플리케이션 서비스 (UseCase 구현체)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthService implements LoginUseCase, RefreshTokenUseCase {

  private final UserPort userPort;
  private final JwtTokenProvider jwtTokenProvider;
  private final JwtProperties jwtProperties;
  private final PasswordEncoder passwordEncoder;

  @Override
  public LoginResponse login(LoginRequest request) {
    // 사용자명으로 사용자 조회
    User user = userPort.findByUsername(request.getUsername())
        .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_CREDENTIALS));

    // 비밀번호 검증
    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new BusinessException(ErrorCode.INVALID_CREDENTIALS);
    }

    // 사용자가 비활성 상태인지 확인
    if (!user.isActive()) {
      throw new BusinessException(ErrorCode.UNAUTHORIZED, "비활성화된 계정입니다");
    }

    // 토큰 생성
    String accessToken = jwtTokenProvider.createAccessToken(user.getId().toString());
    String refreshToken = jwtTokenProvider.createRefreshToken(user.getId().toString());

    log.info("사용자 로그인 성공: userId={}", user.getId());

    return LoginResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .expiresIn(jwtProperties.getAccessExpiration() / 1000)
        .build();
  }

  @Override
  public LoginResponse refreshToken(RefreshTokenRequest request) {
    // Refresh Token 검증
    jwtTokenProvider.validateToken(request.getRefreshToken());
    String userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());

    // 사용자 존재 여부 확인
    User user = userPort.findById(Long.parseLong(userId))
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    // 새 토큰 생성
    String newAccessToken = jwtTokenProvider.createAccessToken(userId);
    String newRefreshToken = jwtTokenProvider.createRefreshToken(userId);

    log.info("토큰 갱신 완료: userId={}", userId);

    return LoginResponse.builder()
        .accessToken(newAccessToken)
        .refreshToken(newRefreshToken)
        .expiresIn(jwtProperties.getAccessExpiration() / 1000)
        .build();
  }
}
