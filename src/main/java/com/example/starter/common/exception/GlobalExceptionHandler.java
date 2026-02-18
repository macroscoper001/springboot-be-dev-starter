package com.example.starter.common.exception;

import com.example.starter.common.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리기
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  /**
   * 비즈니스 예외 처리
   */
  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<?>> handleBusinessException(BusinessException ex) {
    log.warn("비즈니스 예외 발생: code={}, message={}", ex.getCode(), ex.getMessage());
    return ResponseEntity
        .status(ex.getErrorCode().getStatus())
        .body(ApiResponse.fail(ex.getErrorMessage()));
  }

  /**
   * 입력 검증 예외 처리
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  @SuppressWarnings("null")
  public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
    String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("입력값이 유효하지 않습니다");

    log.warn("입력 검증 예외 발생: {}", message);
    return ResponseEntity
        .status(ErrorCode.INVALID_USER_INPUT.getStatus())
        .body(ApiResponse.fail(message));
  }

  /**
   * 일반 예외 처리
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
    log.error("예기치 않은 예외 발생", ex);
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ApiResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR.getMessage()));
  }
}
