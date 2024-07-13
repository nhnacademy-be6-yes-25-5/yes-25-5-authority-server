package com.nhnacademy.yes25.service;

import com.nhnacademy.yes25.common.exception.CustomerIdMisMatchException;
import com.nhnacademy.yes25.common.exception.refreshTokenMisMatchException;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.NoneMemberLoginRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.ZonedDateTime;
import java.util.Optional;
import com.nhnacademy.yes25.application.service.impl.TokenInfoServiceImpl;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TokenInfoServiceImplTest {

    @Mock
    private TokenInfoRepository tokenInfoRepository;

    @Mock
    private JWTUtil jwtUtil;

    @Mock
    private JwtProvider jwtProvider;

    @InjectMocks
    private TokenInfoServiceImpl tokenInfoService;

    private LoginUserResponse loginUserResponse;
    private NoneMemberLoginRequest noneMemberLoginRequest;
    private TokenInfo tokenInfo;

    @BeforeEach
    void setUp() {
        loginUserResponse = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("ACTIVE")
                .build();

        noneMemberLoginRequest = NoneMemberLoginRequest.builder()
                .customerId(100L)
                .role("ROLE_NONE_MEMBER")
                .build();

        tokenInfo = TokenInfo.builder()
                .id(1L)
                .uuid("uuid-1234")
                .customerId(1L)
                .role("ROLE_USER")
                .loginStateName("ACTIVE")
                .refreshToken("refresh-token-1234")
                .createdAt(ZonedDateTime.now())
                .updatedAt(ZonedDateTime.now())
                .build();
    }

    @DisplayName("토큰 정보 생성 - 성공 (기존 정보 삭제)")
    @Test
    void createTokenInfo_success() {
        // given
        LoginUserResponse loginUserResponse = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("ACTIVE")
                .build();
        String accessJwt = "access-token-1234";
        String refreshJwt = "refresh-token-1234";
        when(jwtProvider.getUuidFromToken(accessJwt)).thenReturn("uuid-1234");

        // when
        tokenInfoService.createTokenInfo(loginUserResponse, accessJwt, refreshJwt);

        // then
        verify(tokenInfoRepository, times(1)).deleteAllByCustomerId(loginUserResponse.userId());
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
    }

    @DisplayName("비회원 사용자 토큰 정보 생성 - 성공")
    @Test
    void createTokenInfoNoneMember_success() {
        // given
        noneMemberLoginRequest = NoneMemberLoginRequest.builder()
                .customerId(100L)
                .role("ROLE_NONE_MEMBER")
                .build();
        String accessJwt = "access-token-1234";
        String refreshJwt = "refresh-token-1234";
        when(jwtProvider.getUuidFromToken(accessJwt)).thenReturn("uuid-1234");

        // when
        tokenInfoService.createTokenInfoNoneMember(noneMemberLoginRequest, accessJwt, refreshJwt);

        // then
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
    }

    @DisplayName("사용자 로그인 - 성공")
    @Test
    void doLogin_success() {
        // given
        when(jwtUtil.createAccessJwt()).thenReturn("access-token-1234");
        when(jwtUtil.createRefrshJwt()).thenReturn("refresh-token-1234");

        // when
        AuthResponse authResponse = tokenInfoService.doLogin(loginUserResponse);

        // then
        assertNotNull(authResponse);
        assertEquals("access-token-1234", authResponse.accessToken());
        assertEquals("refresh-token-1234", authResponse.refreshToken());
        verify(tokenInfoRepository, times(2)).deleteAllByCustomerId(loginUserResponse.userId());
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
    }

    @DisplayName("토큰 정보 조회 - 성공 (UUID)")
    @Test
    void getByUuid_success() {
        // given
        when(tokenInfoRepository.findByUuid("uuid-1234"))
                .thenReturn(Optional.of(tokenInfo));

        // when
        ReadTokenInfoResponse readTokenInfoResponse = tokenInfoService.getByUuid("uuid-1234");

        // then
        assertNotNull(readTokenInfoResponse);
        assertEquals(tokenInfo.getId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getCustomerId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getRole(), readTokenInfoResponse.role());
        assertEquals(tokenInfo.getLoginStateName(), readTokenInfoResponse.loginStateName());
        assertEquals(tokenInfo.getRefreshToken(), readTokenInfoResponse.refreshJwt());
    }


    @DisplayName("토큰 정보 조회 - 실패 (UUID 없음)")
    @Test
    void getByUuid_failure_tokenInfoNotFound() {
        // given
        when(tokenInfoRepository.findByUuid("nonexistent-uuid"))
                .thenReturn(Optional.empty());

        // then
        assertThrows(refreshTokenMisMatchException.class,
                () -> tokenInfoService.getByUuid("nonexistent-uuid"));
    }

    @DisplayName("토큰 정보 조회 - 성공 (고객 ID)")
    @Test
    void getByCustomerId_success() {
        // given
        when(tokenInfoRepository.findByCustomerId(loginUserResponse.userId()))
                .thenReturn(Optional.of(tokenInfo));

        // when
        ReadTokenInfoResponse readTokenInfoResponse = tokenInfoService.getByCustomerId(loginUserResponse.userId());

        // then
        assertNotNull(readTokenInfoResponse);
        assertEquals(tokenInfo.getId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getCustomerId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getRole(), readTokenInfoResponse.role());
        assertEquals(tokenInfo.getLoginStateName(), readTokenInfoResponse.loginStateName());
        assertEquals(tokenInfo.getRefreshToken(), readTokenInfoResponse.refreshJwt());
    }

    @DisplayName("토큰 정보 조회 - 성공 (비회원 고객 ID)")
    @Test
    void getByNoneCustomerId_success() {
        // given
        when(tokenInfoRepository.findByCustomerId(loginUserResponse.userId()))
                .thenReturn(Optional.of(tokenInfo));

        // when
        ReadTokenInfoResponse readTokenInfoResponse = tokenInfoService.getByCustomerId(loginUserResponse.userId());

        // then
        assertNotNull(readTokenInfoResponse);
        assertEquals(tokenInfo.getId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getCustomerId(), readTokenInfoResponse.customerId());
        assertEquals(tokenInfo.getRole(), readTokenInfoResponse.role());
        assertEquals(tokenInfo.getLoginStateName(), readTokenInfoResponse.loginStateName());
        assertEquals(tokenInfo.getRefreshToken(), readTokenInfoResponse.refreshJwt());
    }

    @DisplayName("토큰 정보 조회 - 실패 (고객 ID 없음)")
    @Test
    void getByCustomerId_failure_tokenInfoNotFound() {
        // given
        when(tokenInfoRepository.findByCustomerId(100L))
                .thenReturn(Optional.empty());

        // then
        assertThrows(CustomerIdMisMatchException.class,
                () -> tokenInfoService.getByCustomerId(100L));
    }

    @DisplayName("토큰 정보 업데이트 - 성공")
    @Test
    void updateTokenInfo_success() {
        // given
        UpdateTokenInfoRequest updateRequest = UpdateTokenInfoRequest.builder()
                .id(tokenInfo.getId())
                .customerId(tokenInfo.getCustomerId())
                .uuid("new-uuid")
                .role(tokenInfo.getRole())
                .loginStateName(tokenInfo.getLoginStateName())
                .refreshToken("new-refresh-token")
                .createAt(tokenInfo.getCreatedAt())
                .updateAt(ZonedDateTime.now())
                .build();

        // when
        tokenInfoService.updateTokenInfo(updateRequest);

        // then
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
    }

    @DisplayName("만료된 AccessToken 갱신 - 성공")
    @Test
    void updateAccessToken_success() {
        // given
        CreateAccessTokenRequest createAccessTokenRequest = CreateAccessTokenRequest.builder()
                .refreshToken("refresh-token-1234")
                .build();

        when(tokenInfoRepository.findByRefreshToken("refresh-token-1234"))
                .thenReturn(Optional.of(tokenInfo));
        when(jwtUtil.createAccessJwt()).thenReturn("new-access-token");
        when(jwtUtil.createRefrshJwt()).thenReturn("new-refresh-token");
        when(jwtProvider.getUuidFromToken("new-access-token")).thenReturn("new-uuid");

        // when
        AuthResponse authResponse = tokenInfoService.updateAccessToken(createAccessTokenRequest);

        // then
        assertNotNull(authResponse);
        assertEquals("new-access-token", authResponse.accessToken());
        assertEquals("new-refresh-token", authResponse.refreshToken());
        verify(tokenInfoRepository, times(1)).save(any(TokenInfo.class));
    }

    @DisplayName("만료된 AccessToken 갱신 - 실패 (RefreshToken 없음)")
    @Test
    void updateAccessToken_failure_refreshTokenNotFound() {
        // given
        CreateAccessTokenRequest createAccessTokenRequest = CreateAccessTokenRequest.builder()
                .refreshToken("nonexistent-refresh-token")
                .build();

        when(tokenInfoRepository.findByRefreshToken("nonexistent-refresh-token"))
                .thenReturn(Optional.empty());

        // then
        assertThrows(refreshTokenMisMatchException.class,
                () -> tokenInfoService.updateAccessToken(createAccessTokenRequest));
    }

    @DisplayName("토큰 정보 삭제 - 성공 (UUID)")
    @Test
    void removeTokenInfoByUuid_success() {
        // when
        tokenInfoService.removeTokenInfoByUuid("uuid-1234");

        // then
        verify(tokenInfoRepository, times(1)).deleteByUuid("uuid-1234");
    }

    @DisplayName("토큰 정보 삭제 - 성공 (고객 ID)")
    @Test
    void removeTokenAllInfoByCustomerId_success() {
        // when
        tokenInfoService.removeTokenAllInfoByCustomerId(loginUserResponse.userId());

        // then
        verify(tokenInfoRepository, times(1)).deleteAllByCustomerId(loginUserResponse.userId());
    }
}