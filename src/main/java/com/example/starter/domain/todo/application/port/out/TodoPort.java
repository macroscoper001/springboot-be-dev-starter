package com.example.starter.domain.todo.application.port.out;

import com.example.starter.domain.todo.domain.Todo;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * 할일 출력 포트 (저장소 포트)
 */
public interface TodoPort {

  /**
   * 할일 저장
   */
  Todo save(Todo todo);

  /**
   * 할일 조회 (userId로 권한 확인)
   */
  Optional<Todo> findByIdAndUserId(Long todoId, Long userId);

  /**
   * 사용자의 할일 목록 조회
   */
  Page<Todo> findByUserId(Long userId, Pageable pageable);

  /**
   * 사용자의 할일 개수
   */
  long countByUserId(Long userId);

  /**
   * 할일 삭제
   */
  void delete(Todo todo);
}
