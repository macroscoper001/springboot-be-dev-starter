package com.example.starter.security;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * 사용자 상세 정보 서비스
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

  private final UserPort userPort;

  @Override
  public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
    User user = userPort.findById(Long.parseLong(userId))
        .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

    return org.springframework.security.core.userdetails.User.builder()
        .username(user.getId().toString())
        .password(user.getPassword())
        .roles("USER")
        .build();
  }
}
