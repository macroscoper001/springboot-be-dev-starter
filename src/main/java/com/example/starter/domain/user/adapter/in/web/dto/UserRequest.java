package com.example.starter.domain.user.adapter.in.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 요청 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {

  @NotBlank(message = "이메일은 필수입니다")
  @Email(message = "올바른 이메일 형식이 아닙니다")
  private String email;

  @NotBlank(message = "사용자명은 필수입니다")
  private String username;

  @NotBlank(message = "비밀번호는 필수입니다")
  private String password;

  @NotBlank(message = "이름은 필수입니다")
  private String name;
}
