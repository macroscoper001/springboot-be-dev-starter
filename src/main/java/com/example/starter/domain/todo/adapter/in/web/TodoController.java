package com.example.starter.domain.todo.adapter.in.web;

import com.example.starter.common.response.ApiResponse;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoRequest;
import com.example.starter.domain.todo.adapter.in.web.dto.TodoResponse;
import com.example.starter.domain.todo.application.port.in.ChangeTodoStatusUseCase;
import com.example.starter.domain.todo.application.port.in.CreateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.DeleteTodoUseCase;
import com.example.starter.domain.todo.application.port.in.GetTodoUseCase;
import com.example.starter.domain.todo.application.port.in.UpdateTodoUseCase;
import com.example.starter.domain.todo.application.port.in.command.CreateTodoCommand;
import com.example.starter.domain.todo.application.port.in.command.TodoResult;
import com.example.starter.domain.todo.application.port.in.command.UpdateTodoCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 할일 컨트롤러 (입력 어댑터)
 */
@RestController
@RequestMapping("/api/v1/todos")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "할일 API", description = "할일 CRUD 관련 API")
public class TodoController {

  private final CreateTodoUseCase createTodoUseCase;
  private final GetTodoUseCase getTodoUseCase;
  private final UpdateTodoUseCase updateTodoUseCase;
  private final DeleteTodoUseCase deleteTodoUseCase;
  private final ChangeTodoStatusUseCase changeTodoStatusUseCase;

  /**
   * 할일 생성
   */
  @PostMapping
  @Operation(summary = "할일 생성", description = "새로운 할일을 생성합니다")
  public ResponseEntity<ApiResponse<TodoResponse>> createTodo(
      @Valid @RequestBody TodoRequest request,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    CreateTodoCommand command = new CreateTodoCommand(request.getTitle(), request.getDescription());
    TodoResult result = createTodoUseCase.createTodo(userId, command);
    TodoResponse response = TodoResponse.fromTodoResult(result);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "할일이 생성되었습니다"));
  }

  /**
   * 할일 상세 조회
   */
  @GetMapping("/{todoId}")
  @Operation(summary = "할일 상세 조회", description = "할일 ID로 할일 정보를 조회합니다")
  public ResponseEntity<ApiResponse<TodoResponse>> getTodoById(
      @PathVariable Long todoId,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    TodoResult result = getTodoUseCase.getTodoById(todoId, userId);
    TodoResponse response = TodoResponse.fromTodoResult(result);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * 사용자의 할일 목록 조회
   */
  @GetMapping
  @Operation(summary = "할일 목록 조회", description = "사용자의 모든 할일을 페이징하여 조회합니다")
  public ResponseEntity<ApiResponse<Page<TodoResponse>>> getUserTodos(
      Pageable pageable,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    Page<TodoResult> resultPage = getTodoUseCase.getUserTodos(userId, pageable);
    Page<TodoResponse> response = resultPage.map(TodoResponse::fromTodoResult);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * 할일 수정
   */
  @PutMapping("/{todoId}")
  @Operation(summary = "할일 수정", description = "할일 정보를 수정합니다")
  public ResponseEntity<ApiResponse<TodoResponse>> updateTodo(
      @PathVariable Long todoId,
      @Valid @RequestBody TodoRequest request,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    UpdateTodoCommand command = new UpdateTodoCommand(request.getTitle(), request.getDescription());
    TodoResult result = updateTodoUseCase.updateTodo(todoId, userId, command);
    TodoResponse response = TodoResponse.fromTodoResult(result);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "할일이 수정되었습니다"));
  }

  /**
   * 할일 삭제
   */
  @DeleteMapping("/{todoId}")
  @Operation(summary = "할일 삭제", description = "할일을 삭제합니다")
  public ResponseEntity<ApiResponse<Void>> deleteTodo(
      @PathVariable Long todoId,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    deleteTodoUseCase.deleteTodo(todoId, userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(ApiResponse.success(null, "할일이 삭제되었습니다"));
  }

  /**
   * 할일 완료 처리
   */
  @PostMapping("/{todoId}/complete")
  @Operation(summary = "할일 완료", description = "할일을 완료 상태로 변경합니다")
  public ResponseEntity<ApiResponse<TodoResponse>> completeTodo(
      @PathVariable Long todoId,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    TodoResult result = changeTodoStatusUseCase.completeTodo(todoId, userId);
    TodoResponse response = TodoResponse.fromTodoResult(result);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "할일이 완료되었습니다"));
  }

  /**
   * 할일 미완료 처리
   */
  @PostMapping("/{todoId}/pending")
  @Operation(summary = "할일 미완료", description = "할일을 미완료 상태로 변경합니다")
  public ResponseEntity<ApiResponse<TodoResponse>> pendingTodo(
      @PathVariable Long todoId,
      Authentication authentication) {
    Long userId = Long.parseLong(authentication.getName());
    TodoResult result = changeTodoStatusUseCase.pendingTodo(todoId, userId);
    TodoResponse response = TodoResponse.fromTodoResult(result);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "할일이 미완료 처리되었습니다"));
  }
}
