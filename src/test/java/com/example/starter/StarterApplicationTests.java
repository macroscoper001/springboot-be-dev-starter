package com.example.starter;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class StarterApplicationTests {

  @Test
  void contextLoads() {
    // 컨텍스트 로딩 확인 테스트
  }
}
