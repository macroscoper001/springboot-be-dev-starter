package com.example.starter.domain.user.adapter.out.persistence;

import com.example.starter.domain.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 사용자 JPA 리포지토리
 */
@Repository
public interface UserJpaRepository extends JpaRepository<User, Long> {

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
}
