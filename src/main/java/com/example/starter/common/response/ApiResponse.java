package com.example.starter.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 공통 API 응답 래퍼 클래스
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

  private boolean success;
  private T data;
  private String message;
  private LocalDateTime timestamp;

  /**
   * 성공 응답 생성
   */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * 성공 응답 생성 (메시지 포함)
   */
  public static <T> ApiResponse<T> success(T data, String message) {
    return ApiResponse.<T>builder()
        .success(true)
        .data(data)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * 실패 응답 생성
   */
  public static <T> ApiResponse<T> fail(String message) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .timestamp(LocalDateTime.now())
        .build();
  }

  /**
   * 실패 응답 생성 (데이터 포함)
   */
  public static <T> ApiResponse<T> fail(String message, T data) {
    return ApiResponse.<T>builder()
        .success(false)
        .message(message)
        .data(data)
        .timestamp(LocalDateTime.now())
        .build();
  }
}
