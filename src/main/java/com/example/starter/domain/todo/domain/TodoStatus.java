package com.example.starter.domain.todo.domain;

/**
 * 할일 상태 enum
 */
public enum TodoStatus {
  PENDING("미완료"),
  COMPLETED("완료");

  private final String description;

  TodoStatus(String description) {
    this.description = description;
  }

  public String getDescription() {
    return description;
  }
}
