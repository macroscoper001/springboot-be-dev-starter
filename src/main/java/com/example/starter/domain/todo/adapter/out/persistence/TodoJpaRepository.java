package com.example.starter.domain.todo.adapter.out.persistence;

import com.example.starter.domain.todo.domain.Todo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 할일 JPA 리포지토리
 */
@Repository
public interface TodoJpaRepository extends JpaRepository<Todo, Long> {

  /**
   * 사용자의 할일 목록 조회
   */
  Page<Todo> findByUserId(Long userId, Pageable pageable);

  /**
   * 사용자의 특정 할일 조회
   */
  Optional<Todo> findByIdAndUserId(Long todoId, Long userId);

  /**
   * 사용자의 할일 개수
   */
  long countByUserId(Long userId);
}
