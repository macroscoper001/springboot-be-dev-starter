package com.example.starter.domain.user.domain;

import com.example.starter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 사용자 엔티티
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, unique = true)
  private String username;

  @Column(nullable = false)
  private String password;

  @Column(nullable = false)
  private String name;

  @Builder.Default
  @Column(nullable = false)
  private boolean active = true;

  /**
   * 사용자 활성화
   */
  public void activate() {
    this.active = true;
  }

  /**
   * 사용자 비활성화
   */
  public void deactivate() {
    this.active = false;
  }

  /**
   * 비밀번호 변경
   */
  public void updatePassword(String newPassword) {
    this.password = newPassword;
  }

  /**
   * 사용자 프로필 업데이트
   */
  public void updateProfile(String email, String username, String encodedPassword, String name) {
    this.email = email;
    this.username = username;
    this.password = encodedPassword;
    this.name = name;
  }
}
