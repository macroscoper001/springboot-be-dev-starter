package com.example.starter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * JPA 감사 설정
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditConfig {
}
