package com.example.starter.domain.user.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.domain.user.adapter.in.web.dto.UserRequest;
import com.example.starter.domain.user.adapter.in.web.dto.UserResponse;
import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@DisplayName("사용자 서비스 테스트")
class UserServiceTest {

  @Mock
  private UserPort userPort;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserService userService;

  private UserRequest validRequest;
  private User testUser;

  @BeforeEach
  void setUp() {
    validRequest = UserRequest.builder()
        .email("test@example.com")
        .username("testuser")
        .password("password123")
        .name("테스트 사용자")
        .build();

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
    when(userPort.existsByEmail(validRequest.getEmail())).thenReturn(false);
    when(userPort.existsByUsername(validRequest.getUsername())).thenReturn(false);
    when(passwordEncoder.encode(validRequest.getPassword())).thenReturn("encodedPassword");
    when(userPort.save(any(User.class))).thenReturn(testUser);

    // When
    UserResponse response = userService.createUser(validRequest);

    // Then
    assertNotNull(response);
    assertEquals("test@example.com", response.getEmail());
    assertEquals("testuser", response.getUsername());
    assertTrue(response.isActive());
    verify(userPort, times(1)).save(any(User.class));
  }

  @Test
  @DisplayName("사용자 생성 - 이메일 중복")
  void testCreateUser_DuplicateEmail() {
    // Given
    when(userPort.existsByEmail(validRequest.getEmail())).thenReturn(true);

    // When & Then
    assertThrows(BusinessException.class, () -> userService.createUser(validRequest));
  }

  @Test
  @DisplayName("사용자 조회 - 성공")
  void testGetUserById_Success() {
    // Given
    when(userPort.findById(1L)).thenReturn(java.util.Optional.of(testUser));

    // When
    UserResponse response = userService.getUserById(1L);

    // Then
    assertNotNull(response);
    assertEquals(1L, response.getId());
    assertEquals("test@example.com", response.getEmail());
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
