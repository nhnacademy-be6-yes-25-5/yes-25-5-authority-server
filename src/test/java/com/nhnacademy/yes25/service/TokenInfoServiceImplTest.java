package com.nhnacademy.yes25.service;

import com.nhnacademy.yes25.common.exception.refreshTokenMisMatchException;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.domain.UserInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.persistance.repository.UserInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import com.nhnacademy.yes25.application.service.impl.TokenInfoServiceImpl;
import com.nhnacademy.yes25.application.service.impl.TokenInfoDeletionServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenInfoServiceImplTest {

    @Mock
    private TokenInfoRepository tokenInfoRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private JwtProvider jwtProvider;

    @Mock
    private TokenInfoDeletionServiceImpl deletionService;

    @InjectMocks
    private TokenInfoServiceImpl tokenInfoService;

    private LoginUserResponse loginUserResponse;
    private TokenInfo tokenInfo;
    private UserInfo userInfo;

    @BeforeEach
    void setUp() {
        loginUserResponse = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("ACTIVE")
                .build();

        tokenInfo = TokenInfo.builder()
                .id(1L)
                .uuid("uuid-1234")
                .refreshToken("refresh-token-1234")
                .expiryDate(LocalDateTime.now().plusDays(7))
                .build();

        userInfo = UserInfo.builder()
                .uuid("uuid-1234")
                .customerId(1L)
                .role("ROLE_USER")
                .loginStateName("ACTIVE")
                .build();
    }

    @DisplayName("토큰 정보 생성 - 성공")
    @Test
    void createTokenInfo_success() {
        // given
        String uuid = "test-uuid";
        String refreshJwt = "test-refresh-jwt";
        LocalDateTime expirationDate = LocalDateTime.now().plusDays(7);
        when(jwtProvider.getExpirationDateFromToken(refreshJwt)).thenReturn(expirationDate);

        // when
        tokenInfoService.createTokenInfo(uuid, refreshJwt);

        // then
        verify(tokenInfoRepository, times(1)).save(argThat(tokenInfo ->
                tokenInfo.getUuid().equals(uuid) &&
                        tokenInfo.getRefreshToken().equals(refreshJwt) &&
                        tokenInfo.getExpiryDate().equals(expirationDate)
        ));
    }

    @DisplayName("사용자 정보 생성 - 성공")
    @Test
    void createUserInfo_success() {
        // given
        LoginUserResponse user = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("ACTIVE")
                .build();
        String uuid = "test-uuid";

        // when
        tokenInfoService.createUserInfo(user, uuid);

        // then
        verify(userInfoRepository, times(1)).save(argThat(userInfo ->
                userInfo.getUuid().equals(uuid) &&
                        userInfo.getCustomerId().equals(user.userId()) &&
                        userInfo.getRole().equals(user.userRole()) &&
                        userInfo.getLoginStateName().equals(user.loginStatusName())
        ));
    }

    @DisplayName("사용자 로그인 - 성공")
    @Test
    void doLogin_success() {
        // given
        when(jwtProvider.createAccessJwt()).thenReturn("access-token-1234");
        when(jwtProvider.createRefreshJwt()).thenReturn("refresh-token-1234");
        when(jwtProvider.getUuidFromToken("access-token-1234")).thenReturn("uuid-1234");

        // when
        AuthResponse authResponse = tokenInfoService.doLogin(loginUserResponse);

        // then
        assertNotNull(authResponse);
        assertEquals("access-token-1234", authResponse.accessToken());
        assertEquals("refresh-token-1234", authResponse.refreshToken());
        verify(userInfoRepository, times(1)).findByCustomerId(loginUserResponse.userId());
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @DisplayName("토큰 정보 조회 - 성공 (UUID)")
    @Test
    void getByUuid_success() {
        // given
        when(tokenInfoRepository.findByUuid("uuid-1234")).thenReturn(Optional.of(tokenInfo));
        when(userInfoRepository.findByUuid("uuid-1234")).thenReturn(Optional.of(userInfo));

        // when
        ReadTokenInfoResponse readTokenInfoResponse = tokenInfoService.getByUuid("uuid-1234");

        // then
        assertNotNull(readTokenInfoResponse);
        assertEquals(userInfo.getCustomerId(), readTokenInfoResponse.customerId());
        assertEquals(userInfo.getRole(), readTokenInfoResponse.role());
        assertEquals(userInfo.getLoginStateName(), readTokenInfoResponse.loginStateName());
        assertEquals(tokenInfo.getRefreshToken(), readTokenInfoResponse.refreshJwt());
    }

    @DisplayName("토큰 정보 조회 - 실패 (UUID 없음)")
    @Test
    void getByUuid_failure_tokenInfoNotFound() {
        // given
        when(tokenInfoRepository.findByUuid("nonexistent-uuid")).thenReturn(Optional.empty());

        // then
        assertThrows(refreshTokenMisMatchException.class,
                () -> tokenInfoService.getByUuid("nonexistent-uuid"));
    }

    @DisplayName("만료된 AccessToken 갱신 - 성공")
    @Test
    void updateAccessToken_success() {
        // given
        CreateAccessTokenRequest createAccessTokenRequest = new CreateAccessTokenRequest("refresh-token-1234");
        when(tokenInfoRepository.findByRefreshToken("refresh-token-1234")).thenReturn(Optional.of(tokenInfo));
        when(jwtProvider.createAccessJwt()).thenReturn("new-access-token");
        when(jwtProvider.createRefreshJwt()).thenReturn("new-refresh-token");
        when(jwtProvider.getUuidFromToken("new-access-token")).thenReturn("new-uuid");
        when(userInfoRepository.findByUuid("uuid-1234")).thenReturn(Optional.of(userInfo));

        // when
        AuthResponse authResponse = tokenInfoService.updateAccessToken(createAccessTokenRequest);

        // then
        assertNotNull(authResponse);
        assertEquals("new-access-token", authResponse.accessToken());
        assertEquals("new-refresh-token", authResponse.refreshToken());
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
        verify(userInfoRepository, times(1)).save(any(UserInfo.class));
    }

    @DisplayName("만료된 AccessToken 갱신 - 실패 (RefreshToken 없음)")
    @Test
    void updateAccessToken_failure_refreshTokenNotFound() {
        // given
        CreateAccessTokenRequest createAccessTokenRequest = new CreateAccessTokenRequest("nonexistent-refresh-token");
        when(tokenInfoRepository.findByRefreshToken("nonexistent-refresh-token")).thenReturn(Optional.empty());

        // then
        assertThrows(refreshTokenMisMatchException.class,
                () -> tokenInfoService.updateAccessToken(createAccessTokenRequest));
    }

    @DisplayName("기존 사용자 데이터 삭제 - 성공")
    @Test
    void deleteExistingUserData_success() {
        // given
        Long customerId = 1L;
        UserInfo existingUserInfo = UserInfo.builder()
                .uuid("existing-uuid")
                .customerId(customerId)
                .build();
        when(userInfoRepository.findByCustomerId(customerId)).thenReturn(Optional.of(existingUserInfo));

        // when
        tokenInfoService.deleteExistingUserData(customerId);

        // then
        verify(deletionService, times(1)).deleteExistingTokenInfo("existing-uuid");
        verify(deletionService, times(1)).deleteExistingUserInfo("existing-uuid");
    }



    @DisplayName("만료된 토큰 정리 - 성공")
    @Test
    void cleanupExpiredTokens_success() {
        // given
        LocalDateTime now = LocalDateTime.now();
        TokenInfo expiredToken = TokenInfo.builder().uuid("expired-uuid").expiryDate(now.minusDays(1)).build();
        when(tokenInfoRepository.findAllByExpiryDateBefore(any(LocalDateTime.class))).thenReturn(java.util.List.of(expiredToken));

        // when
        tokenInfoService.cleanupExpiredTokens();

        // then
        verify(userInfoRepository, times(1)).deleteByUuid("expired-uuid");
        verify(tokenInfoRepository, times(1)).delete(expiredToken);
    }
}