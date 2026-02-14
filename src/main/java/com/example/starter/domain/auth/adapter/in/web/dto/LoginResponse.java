package com.example.starter.domain.auth.adapter.in.web.dto;

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
}
