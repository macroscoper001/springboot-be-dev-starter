package com.example.starter.domain.todo.adapter.out.persistence;

import com.example.starter.domain.todo.application.port.out.TodoPort;
import com.example.starter.domain.todo.domain.Todo;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 할일 영속성 어댑터 (출력 어댑터)
 */
@Component
@RequiredArgsConstructor
public class TodoPersistenceAdapter implements TodoPort {

  private final TodoJpaRepository todoJpaRepository;

  @Override
  public Todo save(Todo todo) {
    return todoJpaRepository.save(todo);
  }

  @Override
  public Optional<Todo> findByIdAndUserId(Long todoId, Long userId) {
    return todoJpaRepository.findByIdAndUserId(todoId, userId);
  }

  @Override
  public Page<Todo> findByUserId(Long userId, Pageable pageable) {
    return todoJpaRepository.findByUserId(userId, pageable);
  }

  @Override
  public long countByUserId(Long userId) {
    return todoJpaRepository.countByUserId(userId);
  }

  @Override
  public void delete(Todo todo) {
    todoJpaRepository.delete(todo);
  }
}
