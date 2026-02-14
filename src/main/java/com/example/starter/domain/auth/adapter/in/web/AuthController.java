package com.example.starter.domain.auth.adapter.in.web;

import com.example.starter.common.response.ApiResponse;
import com.example.starter.domain.auth.adapter.in.web.dto.LoginRequest;
import com.example.starter.domain.auth.adapter.in.web.dto.LoginResponse;
import com.example.starter.domain.auth.adapter.in.web.dto.RefreshTokenRequest;
import com.example.starter.domain.auth.application.port.in.LoginUseCase;
import com.example.starter.domain.auth.application.port.in.RefreshTokenUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 컨트롤러 (입력 어댑터)
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증 API", description = "로그인, 토큰 갱신 등 인증 관련 API")
public class AuthController {

  private final LoginUseCase loginUseCase;
  private final RefreshTokenUseCase refreshTokenUseCase;

  /**
   * 로그인
   */
  @PostMapping("/login")
  @Operation(summary = "로그인", description = "사용자명과 비밀번호로 로그인하여 JWT 토큰 발급")
  public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
    LoginResponse response = loginUseCase.login(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "로그인에 성공했습니다"));
  }

  /**
   * 토큰 갱신
   */
  @PostMapping("/refresh")
  @Operation(summary = "토큰 갱신", description = "Refresh Token으로 새로운 Access Token 발급")
  public ResponseEntity<ApiResponse<LoginResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
    LoginResponse response = refreshTokenUseCase.refreshToken(request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "토큰이 갱신되었습니다"));
  }
}
