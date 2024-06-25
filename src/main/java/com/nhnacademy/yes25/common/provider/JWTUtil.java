package com.nhnacademy.yes25.common.provider;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    /**
     * 제공된 고객 ID, 사용자 역할, 로그인 상태 이름을 포함하는 새로운 JWT 토큰을 생성합니다.
     *
     * @param customerId JWT 토큰에 포함할 고객 ID
     * @param userRole JWT 토큰에 포함할 사용자 역할
     * @param loginStatusName JWT 토큰에 포함할 로그인 상태 이름
     * @return 새로 생성된 JWT 토큰
     */
    public String createJwt(Long customerId, String userRole, String loginStatusName) {
        return Jwts.builder()
                .setHeaderParam("typ", "JWT")
                .claim("customerId", customerId)
                .claim("userRole", userRole)
                .claim("loginStatusName", loginStatusName)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

}