package com.example.starter.domain.todo.adapter.in.web.dto;

import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.domain.Todo;
import com.example.starter.domain.todo.domain.TodoStatus;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 할일 응답 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TodoResponse {

  private Long id;
  private String title;
  private String description;
  private TodoStatus status;
  private Long userId;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  /**
   * Todo 엔티티에서 DTO로 변환
   */
  public static TodoResponse fromEntity(Todo todo) {
    return TodoResponse.builder()
        .id(todo.getId())
        .title(todo.getTitle())
        .description(todo.getDescription())
        .status(todo.getStatus())
        .userId(todo.getUserId())
        .createdAt(todo.getCreatedAt())
        .updatedAt(todo.getUpdatedAt())
        .build();
  }

  /**
   * TodoResult에서 DTO로 변환
   */
  public static TodoResponse fromTodoResult(TodoResult result) {
    return TodoResponse.builder()
        .id(result.id())
        .title(result.title())
        .description(result.description())
        .status(result.status())
        .userId(result.userId())
        .createdAt(result.createdAt())
        .updatedAt(result.updatedAt())
        .build();
  }
}
