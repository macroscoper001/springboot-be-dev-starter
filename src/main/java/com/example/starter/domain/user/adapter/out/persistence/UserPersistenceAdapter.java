package com.example.starter.domain.user.adapter.out.persistence;

import com.example.starter.domain.user.application.port.out.UserPort;
import com.example.starter.domain.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

/**
 * 사용자 영속성 어댑터 (출력 어댑터)
 */
@Component
@RequiredArgsConstructor
public class UserPersistenceAdapter implements UserPort {

  private final UserJpaRepository userJpaRepository;

  @Override
  public User save(User user) {
    return userJpaRepository.save(user);
  }

  @Override
  public Optional<User> findById(Long id) {
    return userJpaRepository.findById(id);
  }

  @Override
  public Optional<User> findByEmail(String email) {
    return userJpaRepository.findByEmail(email);
  }

  @Override
  public Optional<User> findByUsername(String username) {
    return userJpaRepository.findByUsername(username);
  }

  @Override
  public boolean existsByEmail(String email) {
    return userJpaRepository.existsByEmail(email);
  }

  @Override
  public boolean existsByUsername(String username) {
    return userJpaRepository.existsByUsername(username);
  }

  @Override
  public Page<User> findAll(Pageable pageable) {
    return userJpaRepository.findAll(pageable);
  }

  @Override
  public void delete(User user) {
    userJpaRepository.delete(user);
  }
}
