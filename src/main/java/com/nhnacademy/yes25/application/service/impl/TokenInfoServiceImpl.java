package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.JwtException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenInfoServiceImpl implements TokenInfoService {

    private final JWTUtil jwtUtil;
    private final JwtProvider jwtProvider;
    private final TokenInfoRepository tokenInfoRepository;

    /**
     * 로그인 프로세스를 처리하고 AccessToken과 RefreshToken을 발급합니다.
     *
     * @param user 로그인한 사용자 정보
     * @return 발급된 AccessToken과 RefreshToken 정보
     */
    @Transactional
    @Override
    public AuthResponse doLogin(LoginUserResponse user) {
        String accessToken = jwtUtil.createAccessJwt();
        String refreshToken = jwtUtil.createRefrshJwt();

        Optional<TokenInfo> existingTokenInfo = tokenInfoRepository.findByCustomerId(user.userId());
        if (existingTokenInfo.isPresent()) {
            updateTokenInfo(user, existingTokenInfo.get(), accessToken, refreshToken);
        } else {
            createTokenInfo(user, accessToken, refreshToken);
        }

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    /**
     * RefreshToken을 이용하여 새로운 AccessToken을 발급합니다.
     *
     * @param request RefreshToken 정보
     * @return 새로운 AccessToken과 RefreshToken 정보
     */
    @Transactional
    @Override
    public AuthResponse updateAccessToken(CreateAccessTokenRequest request) {
        TokenInfo tokenInfo = tokenInfoRepository.findByRefreshToken(request.getRefreshToken())
                .orElseThrow(() -> new JwtException(new ErrorStatus("해당 refresh token을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        String newAccessToken = jwtUtil.createAccessJwt();
        String newRefreshToken = jwtUtil.createRefrshJwt();

        UpdateTokenInfoRequest updateRequest = UpdateTokenInfoRequest.builder()
                .id(tokenInfo.getId())
                .customerId(tokenInfo.getCustomerId())
                .uuid(jwtProvider.getUuidFromToken(newAccessToken))
                .role(tokenInfo.getRole())
                .loginStateName(tokenInfo.getLoginStateName())
                .refreshToken(newRefreshToken)
                .createAt(tokenInfo.getCreatedAt())
                .build();
        updateTokenInfo(updateRequest);

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    /**
     * 토큰 정보를 생성합니다.
     *
     * @param user         로그인 사용자 정보
     * @param accessToken  새로 발급된 AccessToken
     * @param refreshToken 새로 발급된 RefreshToken
     */
    @Transactional
    protected void createTokenInfo(LoginUserResponse user, String accessToken, String refreshToken) {
        CreateTokenInfoRequest createRequest = CreateTokenInfoRequest.builder()
                .uuid(jwtProvider.getUuidFromToken(accessToken))
                .customerId(user.userId())
                .role(user.userRole())
                .loginStateName(user.loginStatusName())
                .refreshToken(refreshToken)
                .createAt(ZonedDateTime.now())
                .updateAt(ZonedDateTime.now())
                .build();
        tokenInfoRepository.save(createRequest.toEntity());
    }

    /**
     * 토큰 정보를 업데이트합니다.
     *
     * @param user         로그인 사용자 정보
     * @param existingInfo 기존 토큰 정보
     * @param accessToken  새로 발급된 AccessToken
     * @param refreshToken 새로 발급된 RefreshToken
     */
    @Transactional
    protected void updateTokenInfo(LoginUserResponse user, TokenInfo existingInfo, String accessToken, String refreshToken) {
        UpdateTokenInfoRequest updateRequest = UpdateTokenInfoRequest.builder()
                .id(existingInfo.getId())
                .customerId(user.userId())
                .uuid(jwtProvider.getUuidFromToken(accessToken))
                .role(user.userRole())
                .loginStateName(user.loginStatusName())
                .refreshToken(refreshToken)
                .createAt(existingInfo.getCreatedAt())
                .updateAt(ZonedDateTime.now())
                .build();
        tokenInfoRepository.save(updateRequest.toEntity());
    }

    /**
     * 토큰 정보를 업데이트합니다.
     *
     * @param updateRequest 토큰 정보 업데이트 요청 객체
     */
    @Transactional
    @Override
    public void updateTokenInfo(UpdateTokenInfoRequest updateRequest) {
        tokenInfoRepository.save(updateRequest.toEntity());
    }

    /**
     * 리프레시 토큰으로 토큰 정보를 조회합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return 토큰 정보
     */
    @Override
    public ReadTokenInfoResponse getByRefreshToken(String refreshToken) {
        TokenInfo tokenInfo = tokenInfoRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new JwtException(new ErrorStatus("해당 refresh token을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    /**
     * UUID로 토큰 정보를 조회합니다.
     *
     * @param uuid UUID
     * @return 토큰 정보
     */
    @Transactional(readOnly = true)
    @Override
    public ReadTokenInfoResponse getByUuid(String uuid) {
        TokenInfo tokenInfo = tokenInfoRepository.findByUuid(uuid)
                .orElseThrow(() -> new JwtException(new ErrorStatus("해당 UUID를 찾을 수 없습니다.", 404, LocalDateTime.now())));
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    /**
     * 고객 ID로 토큰 정보를 조회합니다.
     *
     * @param customerId 고객 ID
     * @return 토큰 정보
     */
    @Transactional(readOnly = true)
    @Override
    public ReadTokenInfoResponse getByCustomerId(Long customerId) {
        TokenInfo tokenInfo = tokenInfoRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new JwtException(new ErrorStatus("해당 CustomerId를 찾을 수 없습니다.", 404, LocalDateTime.now())));
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    /**
     * UUID로 토큰 정보를 삭제합니다.
     *
     * @param uuid UUID
     */
    @Transactional
    @Override
    public void removeTokenInfoByUuid(String uuid) {
        tokenInfoRepository.deleteByUuid(uuid);
    }

    /**
     * 고객 ID로 모든 토큰 정보를 삭제합니다.
     *
     * @param customerId 고객 ID
     */
    @Transactional
    @Override
    public void removeTokenAllInfoByCustomerId(Long customerId) {
        tokenInfoRepository.deleteAllByCustomerId(customerId);
    }
}