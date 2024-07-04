package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.UuidMisMatchException;
import com.nhnacademy.yes25.common.exception.CustomerIdMisMatchException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.request.NoneMemberLoginRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import com.nhnacademy.yes25.presentation.dto.response.NoneMemberLoginResponse;

/**
 * 토큰 기반으로 약식의 회원 정보 관리를 위한 서비스 구현 클래스입니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenInfoServiceImpl implements TokenInfoService {

    @PersistenceContext
    private EntityManager entityManager;

    private final JWTUtil jwtUtil;
    private final JwtProvider jwtProvider;
    private final TokenInfoRepository tokenInfoRepository;

    /**
     * 사용자 로그인을 처리하고 새로운 AccessToken과 RefreshToken을 발급합니다.
     *
     * @param user 인증된 사용자 정보
     * @return 새로 발급된 AccessToken과 RefreshToken을 포함한 AuthResponse 객체
     */
    @Transactional
    @Override
    public AuthResponse doLogin(LoginUserResponse user) {

        String accessJwt = jwtUtil.createAccessJwt();
        String refreshJwt = jwtUtil.createRefrshJwt();
        Long customerId = user.userId();

        deleteAndClear(customerId);

        createTokenInfo(user, accessJwt, refreshJwt);

        return AuthResponse.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .build();
    }

    /**
     * 사용자 로그인을 처리하고 새로운 AccessToken과 RefreshToken을 발급합니다.
     *
     * @param request 비회원 사용자의 id
     * @return 새로 발급된 AccessToken과 RefreshToken을 포함한 AuthResponse 객체
     */
    @Transactional
    @Override
    public NoneMemberLoginResponse doLoginNoneMember(NoneMemberLoginRequest request) {
        String accessJwt = jwtUtil.createAccessJwt();
        String refreshJwt = jwtUtil.createRefrshJwt();

        deleteAndClear(request.customerId());

        createTokenInfoNoneMember(request, accessJwt, refreshJwt);

        ReadTokenInfoResponse tokenInfo = getByCustomerId(request.customerId());

        return NoneMemberLoginResponse.builder()
                .accessToken(accessJwt)
                .refreshToken(refreshJwt)
                .customerId(request.customerId())
                .role(tokenInfo.role()).build();
    }


    /**
     * 토큰 정보를 생성합니다.
     *
     * @param user         로그인 사용자 정보
     * @param accessJwt  새로 발급된 AccessToken
     * @param refreshJwt 새로 발급된 RefreshToken
     */
    @Transactional
    @Override
    public void createTokenInfo(LoginUserResponse user, String accessJwt, String refreshJwt) {

        CreateTokenInfoRequest createRequest = CreateTokenInfoRequest.builder()
                .uuid(jwtProvider.getUuidFromToken(accessJwt))
                .customerId(user.userId())
                .role(user.userRole())
                .loginStateName(user.loginStatusName())
                .refreshToken(refreshJwt)
                .createAt(ZonedDateTime.now())
                .updateAt(ZonedDateTime.now())
                .build();
        tokenInfoRepository.save(createRequest.toEntity());
    }

    @Transactional
    @Override
    public void createTokenInfoNoneMember(NoneMemberLoginRequest request, String accessJwt, String refreshJwt) {

        CreateTokenInfoRequest createRequest = CreateTokenInfoRequest.builder()
                .uuid(jwtProvider.getUuidFromToken(accessJwt))
                .customerId(request.customerId())
                .role(request.role())
                .loginStateName("None-Member")
                .refreshToken(refreshJwt)
                .createAt(ZonedDateTime.now())
                .updateAt(ZonedDateTime.now())
                .build();


        getByCustomerId(request.customerId());
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
                .orElseThrow(() -> new UuidMisMatchException(
                                new ErrorStatus("해당 UUID를 찾을 수 없습니다.", 404, LocalDateTime.now())
                        )
                );
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
                .orElseThrow(() -> new CustomerIdMisMatchException(
                                new ErrorStatus("해당 CustomerId를 찾을 수 없습니다.", 404, LocalDateTime.now())
                        )
                );
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
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
     * 만료된 AccessToken을 이용하여 새로운 AccessToken으로 갱신합니다.
     *
     * @param createAccessTokenRequest 만료된 AccessToken 정보를 포함한 요청 객체
     * @return 새로 발급된 AccessToken과 RefreshToken을 포함한 AuthResponse 객체
     * @throws UuidMisMatchException 토큰 정보를 찾을 수 없을 때 발생한 예외
     */
    @Transactional
    @Override
    public AuthResponse updateAccessToken(CreateAccessTokenRequest createAccessTokenRequest) {

        String uuid = jwtProvider.getUuidFromToken(createAccessTokenRequest.expiredAccessJwt());
        TokenInfo tokenInfo = tokenInfoRepository.findByUuid(uuid)
                .orElseThrow(() -> new UuidMisMatchException(
                                new ErrorStatus("해당 uuid로 기존 Token 정보를 찾을 수 없습니다.", 404, LocalDateTime.now())
                        )
                );

        String newAccessJwt = jwtUtil.createAccessJwt();
        String newRefreshJwt = jwtUtil.createRefrshJwt();

        UpdateTokenInfoRequest updateRequest = UpdateTokenInfoRequest.builder()
                .id(tokenInfo.getId())
                .customerId(tokenInfo.getCustomerId())
                .uuid(jwtProvider.getUuidFromToken(newAccessJwt))
                .role(tokenInfo.getRole())
                .loginStateName(tokenInfo.getLoginStateName())
                .refreshToken(newRefreshJwt)
                .createAt(tokenInfo.getCreatedAt())
                .updateAt(ZonedDateTime.now())
                .build();
        updateTokenInfo(updateRequest);

        return AuthResponse.builder()
                .accessToken(newAccessJwt)
                .refreshToken(newRefreshJwt)
                .build();
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

    @Transactional
    public void deleteAndClear(Long customerId) {
        tokenInfoRepository.deleteAllByCustomerId(customerId);
        entityManager.flush();
        entityManager.clear();
    }

}