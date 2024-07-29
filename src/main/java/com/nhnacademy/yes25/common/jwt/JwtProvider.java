package com.nhnacademy.yes25.common.jwt;

import com.nhnacademy.yes25.common.exception.JwtException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtProvider {

    private final SecretKey secretKey;

    @Value("${jwt.access-token.expiration-ms}")
    private int accessTokenExpiration;

    @Value("${jwt.refresh-token.expiration-ms}")
    private int refreshTokenExpiration;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessJwt(LoginUserResponse user) {
        return Jwts.builder()
                .header().add("typ", "ACCESS_TOKEN").and()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .claim("userId", user.userId())
                .claim("userRole", user.userRole())
                .claim("loginStatusName", user.loginStatusName())
                .expiration(Date.from(Instant.now().plusMillis(accessTokenExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public String createRefreshJwt() {
        return Jwts.builder()
                .header().add("typ", "REFRESH_TOKEN").and()
                .subject(UUID.randomUUID().toString())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusMillis(refreshTokenExpiration)))
                .signWith(secretKey)
                .compact();
    }

    public String getUuidFromToken(String token) {
        try {
            return parseToken(token).getSubject();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getSubject();
        }
    }

    public LocalDateTime getExpirationDateFromToken(String token) {
        try {
            Date expiration = parseToken(token).getExpiration();
            return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (ExpiredJwtException e) {
            Date expiration = e.getClaims().getExpiration();
            return expiration.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT token", 401, LocalDateTime.now()));
        }
    }

    private Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (JwtException e) {
            throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT token", 401, LocalDateTime.now()));
        }
    }

    public String extractTokenFromHeader(String header) {
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new JwtException(ErrorStatus.toErrorStatus("Invalid authorization header", 401, LocalDateTime.now()));
    }

    public LoginUserResponse getLoginUserFromToken(String token) {
        try {
            Claims claims = getAllClaimsFromToken(token);

            Long userId = claims.get("userId", Long.class);
            String userRole = claims.get("userRole", String.class);
            String loginStatusName = claims.get("loginStatusName", String.class);

            if (userId == null || userRole == null || loginStatusName == null) {
                throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT claims", 401, LocalDateTime.now()));
            }

            return new LoginUserResponse(userId, userRole, loginStatusName);
        } catch (ExpiredJwtException e) {
            // 만료된 토큰의 경우에도 사용자 정보를 반환할 수 있습니다.
            Claims claims = e.getClaims();
            Long userId = claims.get("userId", Long.class);
            String userRole = claims.get("userRole", String.class);
            String loginStatusName = claims.get("loginStatusName", String.class);

            if (userId == null || userRole == null || loginStatusName == null) {
                throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT claims", 401, LocalDateTime.now()));
            }

            return new LoginUserResponse(userId, userRole, loginStatusName);
        } catch (JwtException e) {
            throw new JwtException(ErrorStatus.toErrorStatus("Invalid JWT token", 401, LocalDateTime.now()));
        }
    }
}