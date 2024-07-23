package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.refreshTokenMisMatchException;
import com.nhnacademy.yes25.common.exception.CustomerIdMisMatchException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.domain.UserInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.persistance.repository.UserInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

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
    private final TokenInfoRepository tokenInfoRepository;
    private final UserInfoRepository userInfoRepository;
    private final TokenInfoDeletionServiceImpl deletionService;

    /**
     * 사용자 로그인을 처리하고 인증 토큰을 생성합니다.
     *
     * @param user 로그인한 사용자 정보
     * @return AuthResponse 생성된 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @Override
    public AuthResponse doLogin(LoginUserResponse user) {

        deleteExistingUserData(user.userId());
        String accessJwt = jwtProvider.createAccessJwt();
        String refreshJwt = jwtProvider.createRefreshJwt();
        String uuid = jwtProvider.getUuidFromToken(accessJwt);

        createTokenInfo(uuid, refreshJwt);
        createUserInfo(user, uuid);

        return AuthResponse.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    /**
     * 기존 사용자 데이터를 삭제합니다.
     *
     * @param customerId 삭제할 사용자의 ID
     */
    @Override
    public void deleteExistingUserData(Long customerId) {
        userInfoRepository.findByCustomerId(customerId).ifPresent(userInfo -> {
            deletionService.deleteExistingTokenInfo(userInfo.getUuid());
            deletionService.deleteExistingUserInfo(userInfo.getUuid());
        });
    }

    /**
     * 토큰 정보를 생성하고 저장합니다.
     *
     * @param uuid UUID
     * @param refreshJwt 리프레시 토큰
     */
    @Override
    public void createTokenInfo(String uuid, String refreshJwt) {
        TokenInfo tokenInfo = TokenInfo.builder()
                .uuid(uuid)
                .refreshToken(refreshJwt)
                .expiryDate(jwtProvider.getExpirationDateFromToken(refreshJwt))
                .build();
        tokenInfoRepository.save(tokenInfo);
    }

    /**
     * 사용자 정보를 생성하고 저장합니다.
     *
     * @param user 로그인한 사용자 정보
     * @param uuid UUID
     */
    @Override
    public void createUserInfo(LoginUserResponse user, String uuid) {

        UserInfo userInfo = UserInfo.builder()
                .uuid(uuid)
                .customerId(user.userId())
                .role(user.userRole())
                .loginStateName(user.loginStatusName())
                .build();
        userInfoRepository.save(userInfo);
    }

    /**
     * UUID로 토큰 정보와 사용자 정보를 조회합니다.
     *
     * @param uuid UUID
     * @return ReadTokenInfoResponse 조회된 토큰 정보와 사용자 정보
     */
    @Override
    @Transactional(readOnly = true)
    public ReadTokenInfoResponse getByUuid(String uuid) {

        TokenInfo tokenInfo = getTokenInfoByUuid(uuid);
        UserInfo userInfo = getUserInfoByUuid(uuid);

        return ReadTokenInfoResponse.fromEntity(userInfo, tokenInfo.getRefreshToken());
    }

    /**
     * 액세스 및 리프레시 토큰을 갱신합니다.
     *
     * @param createAccessTokenRequest 액세스 토큰 생성 요청
     * @return AuthResponse 갱신된 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @Override
    public AuthResponse updateAccessToken(CreateAccessTokenRequest createAccessTokenRequest) {

        TokenInfo oldTokenInfo = getTokenInfoByRefreshToken(createAccessTokenRequest.refreshToken());

        String newAccessJwt = jwtProvider.createAccessJwt();
        String newRefreshJwt = jwtProvider.createRefreshJwt();
        String oldUuid = oldTokenInfo.getUuid();
        String newUuid = jwtProvider.getUuidFromToken(newAccessJwt);

        updateTokenInfo(oldTokenInfo, newUuid, newRefreshJwt);
        updateUserInfo(oldUuid, newUuid);

        return AuthResponse.builder()
                .accessToken(newAccessJwt)
                .refreshToken(newRefreshJwt)
                .build();
    }

    /**
     * UUID를 사용하여 TokenInfo를 조회합니다.
     *
     * @param uuid 조회할 TokenInfo의 UUID
     * @return TokenInfo 조회된 토큰 정보
     * @throws refreshTokenMisMatchException UUID에 해당하는 TokenInfo가 없을 경우 발생
     */
    @Override
    public TokenInfo getTokenInfoByUuid(String uuid) {
        return tokenInfoRepository.findByUuid(uuid)
                .orElseThrow(() -> new refreshTokenMisMatchException(
                        ErrorStatus.toErrorStatus("해당 UUID로 Token Info를 찾을 수 없습니다.", 404, LocalDateTime.now())));
    }

    /**
     * UUID를 사용하여 UserInfo를 조회합니다.
     *
     * @param uuid 조회할 UserInfo의 UUID
     * @return UserInfo 조회된 사용자 정보
     * @throws CustomerIdMisMatchException UUID에 해당하는 UserInfo가 없을 경우 발생
     */
    @Override
    public UserInfo getUserInfoByUuid(String uuid) {
        return userInfoRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomerIdMisMatchException(
                        ErrorStatus.toErrorStatus("해당 UUID로 User Info를 찾을 수 없습니다.", 404, LocalDateTime.now())));
    }

    /**
     * Refresh 토큰을 사용하여 TokenInfo를 조회합니다.
     *
     * @param refreshToken 조회할 TokenInfo의 Refresh 토큰
     * @return TokenInfo 조회된 토큰 정보
     * @throws refreshTokenMisMatchException Refresh 토큰에 해당하는 TokenInfo가 없을 경우 발생
     */
    @Override
    public TokenInfo getTokenInfoByRefreshToken(String refreshToken) {
        return tokenInfoRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new refreshTokenMisMatchException(
                        ErrorStatus.toErrorStatus("해당 refresh Token으로 기존 Token 정보를 찾을 수 없습니다.", 404, LocalDateTime.now())));
    }

    /**
     * TokenInfo를 업데이트합니다.
     *
     * @param oldTokenInfo 업데이트할 기존 TokenInfo
     * @param newUuid 새로운 UUID
     * @param newRefreshJwt 새로운 Refresh 토큰
     */
    @Override
    public void updateTokenInfo(TokenInfo oldTokenInfo, String newUuid, String newRefreshJwt) {

        TokenInfo newTokenInfo = TokenInfo.builder()
                .id(oldTokenInfo.getId())
                .uuid(newUuid)
                .refreshToken(newRefreshJwt)
                .expiryDate(jwtProvider.getExpirationDateFromToken(newRefreshJwt)).build();
        tokenInfoRepository.save(newTokenInfo);
    }

    /**
     * UserInfo를 업데이트합니다.
     * 기존 UserInfo를 삭제하고 새로운 UUID로 새 UserInfo를 생성합니다.
     *
     * @param oldUuid 기존 UserInfo의 UUID
     * @param newUuid 새로운 UUID
     */
    @Override
    public void updateUserInfo(String oldUuid, String newUuid) {

        UserInfo oldUserInfo = getUserInfoByUuid(oldUuid);
        deletionService.deleteExistingUserInfo(oldUuid);
        UserInfo newUserInfo = UserInfo.builder()
                .uuid(newUuid)
                .customerId(oldUserInfo.getCustomerId())
                .role(oldUserInfo.getRole())
                .loginStateName(oldUserInfo.getLoginStateName())
                .build();
        userInfoRepository.save(newUserInfo);
    }

    /**
     * 만료된 토큰과 관련 사용자 정보를 정리합니다.
     * 이 메소드는 12시간마다 실행됩니다.
     */
    @Override
    @Scheduled(cron = "0 0 */12 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        List<TokenInfo> expiredTokens = tokenInfoRepository.findAllByExpiryDateBefore(now);

        for (TokenInfo tokenInfo : expiredTokens) {
            String uuid = tokenInfo.getUuid();
            userInfoRepository.deleteByUuid(uuid);
            tokenInfoRepository.delete(tokenInfo);
        }

        log.info("만료된 토큰 {}개와 관련 사용자 정보를 정리했습니다.", expiredTokens.size());
    }


}