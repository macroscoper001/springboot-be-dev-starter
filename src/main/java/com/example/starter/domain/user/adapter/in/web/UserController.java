package com.example.starter.domain.user.adapter.in.web;

import com.example.starter.common.response.ApiResponse;
import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.adapter.in.web.dto.UserResponse;
import com.example.starter.domain.user.application.port.in.CreateUserUseCase;
import com.example.starter.domain.user.application.port.in.DeleteUserUseCase;
import com.example.starter.domain.user.application.port.in.GetUserUseCase;
import com.example.starter.domain.user.application.port.in.UpdateUserUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 사용자 컨트롤러 (입력 어댑터)
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
@Tag(name = "사용자 API", description = "사용자 CRUD 관련 API")
public class UserController {

  private final CreateUserUseCase createUserUseCase;
  private final GetUserUseCase getUserUseCase;
  private final UpdateUserUseCase updateUserUseCase;
  private final DeleteUserUseCase deleteUserUseCase;

  /**
   * 사용자 생성
   */
  @PostMapping
  @Operation(summary = "사용자 생성", description = "새로운 사용자를 생성합니다")
  public ResponseEntity<ApiResponse<UserResponse>> createUser(@Valid @RequestBody UserRequest request) {
    UserResponse response = createUserUseCase.createUser(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ApiResponse.success(response, "사용자가 생성되었습니다"));
  }

  /**
   * 사용자 상세 조회
   */
  @GetMapping("/{userId}")
  @Operation(summary = "사용자 상세 조회", description = "사용자 ID로 사용자 정보를 조회합니다")
  public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long userId) {
    UserResponse response = getUserUseCase.getUserById(userId);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * 모든 사용자 조회
   */
  @GetMapping
  @Operation(summary = "모든 사용자 조회", description = "모든 사용자를 페이징하여 조회합니다")
  public ResponseEntity<ApiResponse<Page<UserResponse>>> getAllUsers(Pageable pageable) {
    Page<UserResponse> response = getUserUseCase.getAllUsers(pageable);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response));
  }

  /**
   * 사용자 정보 업데이트
   */
  @PutMapping("/{userId}")
  @Operation(summary = "사용자 정보 업데이트", description = "사용자 정보를 업데이트합니다")
  public ResponseEntity<ApiResponse<UserResponse>> updateUser(
      @PathVariable Long userId,
      @Valid @RequestBody UserRequest request) {
    UserResponse response = updateUserUseCase.updateUser(userId, request);
    return ResponseEntity
        .status(HttpStatus.OK)
        .body(ApiResponse.success(response, "사용자 정보가 업데이트되었습니다"));
  }

  /**
   * 사용자 삭제
   */
  @DeleteMapping("/{userId}")
  @Operation(summary = "사용자 삭제", description = "사용자를 삭제합니다")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long userId) {
    deleteUserUseCase.deleteUser(userId);
    return ResponseEntity
        .status(HttpStatus.NO_CONTENT)
        .body(ApiResponse.success(null, "사용자가 삭제되었습니다"));
  }
}
