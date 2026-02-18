package com.example.starter.domain.user.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.domain.user.application.port.in.command.CreateUserCommand;
import com.example.starter.domain.user.application.port.in.command.UserResult;
import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 테스트")
@SuppressWarnings("null")
class UserServiceTest {

  @Mock
  private UserPort userPort;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private CreateUserCommand validCommand;
  private User testUser;

  @BeforeEach
  void setUp() {
    validCommand = new CreateUserCommand(
        "test@example.com",
        "testuser",
        "password123",
        "테스트 사용자"
    );

    testUser = User.builder()
        .id(1L)
        .email("test@example.com")
        .username("testuser")
        .password("encodedPassword")
        .name("테스트 사용자")
        .active(true)
        .build();
  }

  @Test
  @DisplayName("사용자 생성 - 성공")
  void testCreateUser_Success() {
    // Given
    when(userPort.existsByEmail(validCommand.email())).thenReturn(false);
    when(userPort.existsByUsername(validCommand.username())).thenReturn(false);
    when(passwordEncoder.encode(validCommand.password())).thenReturn("encodedPassword");
    when(userPort.save(any(User.class))).thenReturn(testUser);

    // When
    UserResult result = userService.createUser(validCommand);

    // Then
    assertNotNull(result);
    assertEquals("test@example.com", result.email());
    assertEquals("testuser", result.username());
    assertTrue(result.active());
    verify(userPort, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 - 이메일 중복")
  void testCreateUser_DuplicateEmail() {
    // Given
    when(userPort.existsByEmail(validCommand.email())).thenReturn(true);

    // When & Then
    assertThrows(BusinessException.class, () -> userService.createUser(validCommand));
  }

  @Test
  @DisplayName("사용자 조회 - 성공")
  void testGetUserById_Success() {
    // Given
    when(userPort.findById(1L)).thenReturn(java.util.Optional.of(testUser));

    // When
    UserResult result = userService.getUserById(1L);

    // Then
    assertNotNull(result);
    assertEquals(1L, result.id());
    assertEquals("test@example.com", result.email());
  }

  @Test
  @DisplayName("사용자 조회 - 사용자 없음")
  void testGetUserById_NotFound() {
    // Given
    when(userPort.findById(999L)).thenReturn(java.util.Optional.empty());

    // When & Then
    assertThrows(BusinessException.class, () -> userService.getUserById(999L));
  }
}
