package com.nhnacademy.yes25.common.provider;

import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * JWTUtil 클래스는 JSON Web Token(JWT) 생성, 파싱, 검증 기능을 제공하는 유틸리티 클래스입니다.
 *
 * @author lettuce82
 * @version 1.0
 */
@Component
public class JWTUtil {

    private final SecretKey secretKey;

    @Value("${jwt.expiration-ms}")
    private long JWT_EXPIRATION_MS; // 30분

    /**
     * 제공된 비밀 키를 사용하여 JWTUtil 인스턴스를 생성합니다.
     *
     * @param jwtSecret JWT 토큰 서명 및 검증에 사용되는 비밀 키
     */
    public JWTUtil(@Value("${jwt.secret}") String jwtSecret) {
        secretKey = new SecretKeySpec(jwtSecret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createAccessJwt() {
        return Jwts.builder()
                .header().add("typ", "ACCESS_TOKEN").and()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(JWT_EXPIRATION_MS)))
                .signWith(secretKey)
                .compact();
    }

    public String createRefrshJwt() {
        return Jwts.builder()
                .header().add("typ", "REFRESH_TOKEN").and()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(JWT_EXPIRATION_MS)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * JWT 토큰에서 UUID를 추출합니다.
     *
     * @param token JWT 토큰
     * @return 토큰에 포함된 UUID
     * @throws JwtException 토큰이 유효하지 않거나 파싱 중 오류가 발생한 경우
     */
    public String getUuidFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            return claims.getSubject();
        } catch (Exception e) {
            throw new JwtException("Invalid token", e);
        }
    }

    /**
     * JWT 토큰의 유효성을 검사합니다.
     *
     * @param token JWT 토큰
     * @return 토큰이 유효하면 true, 그렇지 않으면 false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}