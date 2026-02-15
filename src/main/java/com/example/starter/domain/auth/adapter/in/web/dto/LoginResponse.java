package com.example.starter.domain.auth.adapter.in.web.dto;

import com.example.starter.domain.auth.application.port.in.command.AuthResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {

  private String accessToken;
  private String refreshToken;
  @Default
  private String tokenType = "Bearer";
  private long expiresIn;

  /**
   * AuthResult에서 DTO로 변환
   */
  public static LoginResponse fromAuthResult(AuthResult result) {
    return LoginResponse.builder()
        .accessToken(result.accessToken())
        .refreshToken(result.refreshToken())
        .expiresIn(result.expiresIn())
        .build();
  }
}
