package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
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
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenInfoServiceImpl implements TokenInfoService {

    private final JWTUtil jwtUtil;
    private final TokenInfoRepository tokenInfoRepository;

    @Transactional
    @Override
    public AuthResponse doLogin(LoginUserResponse user) throws ApplicationException {

        String accessJwt = jwtUtil.createAccessJwt();
        String refreshJwt = jwtUtil.createRefrshJwt();

        // 기존 토큰 정보 가져오기
        Optional<TokenInfo> existingTokenInfo = tokenInfoRepository.findByCustomerId(user.userId());

        if (existingTokenInfo.isPresent()) {
            // 기존 토큰이 있을 경우, 갱신
            UpdateTokenInfoRequest updateRequest = UpdateTokenInfoRequest.builder()
                    .id(existingTokenInfo.get().getId())
                    .customerId(user.userId())
                    .uuid(jwtUtil.getUuidFromToken(accessJwt))
                    .role(user.userRole())
                    .loginStateName(user.loginStatusName())
                    .refreshToken(refreshJwt)
                    .createAt(existingTokenInfo.get().getCreatedAt())
                    .build();
            updateTokenInfo(updateRequest);
        } else {
            // 기존 토큰이 없을 경우, 새로 생성
            CreateTokenInfoRequest createRequest = CreateTokenInfoRequest.builder()
                    .uuid(jwtUtil.getUuidFromToken(accessJwt))
                    .customerId(user.userId())
                    .role(user.userRole())
                    .loginStateName(user.loginStatusName())
                    .refreshToken(refreshJwt)
                    .build();
            createTokenInfo(createRequest);
        }

        return AuthResponse.builder().accessToken(accessJwt).refreshToken(refreshJwt).build();
    }

    @Transactional
    @Override
    public void createTokenInfo(CreateTokenInfoRequest createTokenInfoRequest) {
        tokenInfoRepository.save(createTokenInfoRequest.toEntity());
    }

    @Transactional(readOnly = true)
    @Override
    public ReadTokenInfoResponse getByUuid(String uuid) {
        TokenInfo tokenInfo = tokenInfoRepository.findByUuid(uuid).orElseThrow(() ->
                new ApplicationException(ErrorStatus.toErrorStatus("토큰을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    @Transactional(readOnly = true)
    @Override
    public ReadTokenInfoResponse getByCustomerId(Long customerId) {
        TokenInfo tokenInfo = tokenInfoRepository.findByCustomerId(customerId).orElseThrow(() ->
                new ApplicationException(ErrorStatus.toErrorStatus("토큰을 찾을 수 없습니다.", 404, LocalDateTime.now())));
        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    @Override
    public void updateTokenInfo(UpdateTokenInfoRequest updateTokenInfoRequest) {
        tokenInfoRepository.save(updateTokenInfoRequest.toEntity());
    }

    @Transactional
    @Override
    public void removeTokenInfoByUuid(String uuid) {
        tokenInfoRepository.deleteByUuid(uuid);
    }

    @Transactional
    @Override
    public void removeTokenAllInfoByCustomerId(Long customerId) {
        tokenInfoRepository.deleteAllByCustomerId(customerId);
    }
}
