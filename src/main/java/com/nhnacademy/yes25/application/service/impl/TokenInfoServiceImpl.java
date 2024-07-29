package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.refreshTokenMisMatchException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * TokenInfoServiceImpl 클래스는 토큰 기반의 회원 정보 관리를 위한 서비스를 구현합니다.
 * 이 클래스는 로그인, 토큰 생성, 토큰 갱신, 회원 정보 조회 등의 기능을 제공합니다.
 *
 * @author Chaesanghui
 * @version 1.0
 */
@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class TokenInfoServiceImpl implements TokenInfoService {

    private final JwtProvider jwtProvider;
    private final RedisTemplate<String, String> redisTemplate;
    @Value("${jwt.refresh-token.expiration-ms}")
    private int refreshTokenExpiration;

    @Override
    public AuthResponse doLogin(LoginUserResponse user) {
        deleteExistingUserData(user.userId());
        String accessJwt = jwtProvider.createAccessJwt(user);
        String refreshJwt = jwtProvider.createRefreshJwt();

        saveRefreshToken(refreshJwt, user.userId());

        return AuthResponse.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    @Override
    public void deleteExistingUserData(Long userId) {
        String userKey = "user:" + userId;
        String existingRefreshToken = redisTemplate.opsForValue().get(userKey);
        if (existingRefreshToken != null) {
            redisTemplate.delete(existingRefreshToken);
            redisTemplate.delete(userKey);
        }
    }

    private void saveRefreshToken(String refreshToken, Long userId) {
        redisTemplate.opsForValue().set(
                refreshToken,
                "valid",
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
        redisTemplate.opsForValue().set("user:" + userId, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isRefreshTokenValid(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(refreshToken));
    }

    @Override
    public AuthResponse updateAccessToken(String oldRefreshToken,
                                          LoginUserResponse user) {
        if (!isRefreshTokenValid(oldRefreshToken)) {
            throw new refreshTokenMisMatchException(
                    ErrorStatus.toErrorStatus("유효하지 않은 Refresh Token입니다.", 404, LocalDateTime.now()));
        }

        String newAccessJwt = jwtProvider.createAccessJwt(user);
        String newRefreshJwt = jwtProvider.createRefreshJwt();

        String userId = findUserIdByRefreshToken(oldRefreshToken);

        saveRefreshToken(newRefreshJwt, Long.parseLong(userId));
        redisTemplate.delete(oldRefreshToken);

        return AuthResponse.builder()
                .accessToken(newAccessJwt)
                .refreshToken(newRefreshJwt)
                .build();
    }

    private String findUserIdByRefreshToken(String refreshToken) {
        Set<String> keys = redisTemplate.keys("user:*");
        for (String key : keys) {
            String storedRefreshToken = redisTemplate.opsForValue().get(key);
            if (refreshToken.equals(storedRefreshToken)) {
                return key.split(":")[1];
            }
        }
        throw new refreshTokenMisMatchException(
                ErrorStatus.toErrorStatus("해당 Refresh Token에 대한 사용자를 찾을 수 없습니다.", 404, LocalDateTime.now()));
    }
}