package com.example.starter.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * 에러 코드 정의
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 인증 및 권한
  UNAUTHORIZED("AUTH_001", "인증이 필요합니다", HttpStatus.UNAUTHORIZED),
  FORBIDDEN("AUTH_002", "접근 권한이 없습니다", HttpStatus.FORBIDDEN),
  INVALID_TOKEN("AUTH_003", "유효하지 않은 토큰입니다", HttpStatus.UNAUTHORIZED),
  TOKEN_EXPIRED("AUTH_004", "토큰이 만료되었습니다", HttpStatus.UNAUTHORIZED),
  INVALID_CREDENTIALS("AUTH_005", "잘못된 계정 정보입니다", HttpStatus.UNAUTHORIZED),

  // 사용자 관련
  USER_NOT_FOUND("USER_001", "사용자를 찾을 수 없습니다", HttpStatus.NOT_FOUND),
  USER_ALREADY_EXISTS("USER_002", "이미 존재하는 사용자입니다", HttpStatus.CONFLICT),
  INVALID_USER_INPUT("USER_003", "잘못된 사용자 정보입니다", HttpStatus.BAD_REQUEST),

  // 요청 관련
  BAD_REQUEST("REQUEST_001", "잘못된 요청입니다", HttpStatus.BAD_REQUEST),
  INVALID_PARAMETER("REQUEST_002", "잘못된 파라미터입니다", HttpStatus.BAD_REQUEST),
  MISSING_PARAMETER("REQUEST_003", "필수 파라미터가 누락되었습니다", HttpStatus.BAD_REQUEST),

  // 서버 오류
  INTERNAL_SERVER_ERROR("SERVER_001", "서버 내부 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR),
  DATABASE_ERROR("SERVER_002", "데이터베이스 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);

  private final String code;
  private final String message;
  private final HttpStatus status;
}
