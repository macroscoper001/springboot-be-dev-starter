package com.example.starter.security;

import com.example.starter.common.exception.BusinessException;
import com.example.starter.common.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * JWT 토큰 생성 및 검증 로직
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {

  private final JwtProperties jwtProperties;

  /**
   * Access Token 생성
   */
  public String createAccessToken(String userId) {
    return createToken(userId, jwtProperties.getAccessExpiration());
  }

  /**
   * Refresh Token 생성
   */
  public String createRefreshToken(String userId) {
    return createToken(userId, jwtProperties.getRefreshExpiration());
  }

  /**
   * 토큰 생성
   */
  private String createToken(String userId, long expiration) {
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expiration);

    SecretKey key = getSigningKey();

    return Jwts.builder()
        .subject(userId)
        .issuedAt(now)
        .expiration(expiryDate)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  /**
   * 토큰에서 사용자 ID 추출
   */
  public String getUserIdFromToken(String token) {
    try {
      Claims claims = Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token)
          .getPayload();
      return claims.getSubject();
    } catch (JwtException | IllegalArgumentException ex) {
      log.warn("토큰에서 사용자 ID 추출 실패: {}", ex.getMessage());
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
  }

  /**
   * 토큰 유효성 검증
   */
  public boolean validateToken(String token) {
    try {
      Jwts.parser()
          .verifyWith(getSigningKey())
          .build()
          .parseSignedClaims(token);
      return true;
    } catch (io.jsonwebtoken.ExpiredJwtException ex) {
      log.warn("토큰이 만료되었습니다");
      throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
    } catch (JwtException | IllegalArgumentException ex) {
      log.warn("유효하지 않은 토큰: {}", ex.getMessage());
      throw new BusinessException(ErrorCode.INVALID_TOKEN);
    }
  }

  /**
   * 서명 키 생성
   */
  private SecretKey getSigningKey() {
    byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
    return new SecretKeySpec(keyBytes, 0, keyBytes.length, "HmacSHA256");
  }
}
