package com.example.starter.domain.user.adapter.in.web.dto;

import com.example.starter.domain.user.application.port.in.command.UserResult;
import com.example.starter.domain.user.domain.User;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

  private Long id;
  private String email;
  private String username;
  private String name;
  private boolean active;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * User 엔티티에서 DTO로 변환
   */
  public static UserResponse fromEntity(User user) {
    return UserResponse.builder()
        .id(user.getId())
        .email(user.getEmail())
        .username(user.getUsername())
        .name(user.getName())
        .active(user.isActive())
        .createdAt(user.getCreatedAt())
        .updatedAt(user.getUpdatedAt())
        .build();
  }

  /**
   * UserResult에서 DTO로 변환
   */
  public static UserResponse fromUserResult(UserResult result) {
    return UserResponse.builder()
        .id(result.id())
        .email(result.email())
        .username(result.username())
        .name(result.name())
        .active(result.active())
        .createdAt(result.createdAt())
        .updatedAt(result.updatedAt())
        .build();
  }
}
