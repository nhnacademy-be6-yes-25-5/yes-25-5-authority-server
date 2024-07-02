package com.nhnacademy.yes25.common.provider;

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
 * JWTUtil 클래스는 JSON Web Token(JWT) 생성 기능을 제공하는 유틸리티 클래스입니다.
 *
 * @author lettuce82
 * @version 1.0
 */
@Component
public class JWTUtil {

    private final SecretKey secretKey;

    @Value("${jwt.access-token.expiration-ms}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-ms}")
    private int refreshTokenExpiration;

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
                .expiration(Date.from(Instant.now().plusMillis(accessTokenExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public String createRefrshJwt() {
        return Jwts.builder()
                .header().add("typ", "REFRESH_TOKEN").and()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(refreshTokenExpiration)))
                .signWith(secretKey)
                .compact();
    }

}