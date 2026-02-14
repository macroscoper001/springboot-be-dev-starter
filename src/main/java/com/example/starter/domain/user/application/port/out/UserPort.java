package com.example.starter.domain.user.application.port.out;

import com.example.starter.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 사용자 출력 포트 (저장소 포트)
 */
public interface UserPort {

  /**
   * 사용자 저장
   */
  User save(User user);

  /**
   * 사용자 ID로 조회
   */
  Optional<User> findById(Long id);

  /**
   * 이메일로 사용자 조회
   */
  Optional<User> findByEmail(String email);

  /**
   * 사용자명으로 사용자 조회
   */
  Optional<User> findByUsername(String username);

  /**
   * 이메일 존재 여부 확인
   */
  boolean existsByEmail(String email);

  /**
   * 사용자명 존재 여부 확인
   */
  boolean existsByUsername(String username);

  /**
   * 모든 사용자 조회 (페이징)
   */
  Page<User> findAll(Pageable pageable);

  /**
   * 사용자 삭제
   */
  void delete(User user);
}
