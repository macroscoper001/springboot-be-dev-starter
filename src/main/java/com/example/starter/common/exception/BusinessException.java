package com.example.starter.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 비즈니스 예외
 */
@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {

  private final ErrorCode errorCode;

  public BusinessException(ErrorCode errorCode, String message) {
    super(message);
    this.errorCode = errorCode;
  }

  public String getCode() {
    return errorCode.getCode();
  }

  public String getErrorMessage() {
    return errorCode.getMessage();
  }
}
