package com.example.starter.domain.todo.domain;

import com.example.starter.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 할일 엔티티
 */
@Entity
@Table(name = "todos")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Todo extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private TodoStatus status = TodoStatus.PENDING;

  @Column(nullable = false)
  private Long userId;

  /**
   * 할일 제목 변경
   */
  public void updateTitle(String title) {
    this.title = title;
  }

  /**
   * 할일 설명 변경
   */
  public void updateDescription(String description) {
    this.description = description;
  }

  /**
   * 할일 상태 변경
   */
  public void changeStatus(TodoStatus status) {
    this.status = status;
  }

  /**
   * 할일 완료 처리
   */
  public void complete() {
    this.status = TodoStatus.COMPLETED;
  }

  /**
   * 할일 미완료 처리
   */
  public void pending() {
    this.status = TodoStatus.PENDING;
  }
}
