package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.repository.TokenInfoRepository;
import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenInfoServiceImpl implements TokenInfoService {

    private final TokenInfoRepository tokenInfoRepository;

    @Transactional
    @Override
    public void createTokenInfo(CreateTokenInfoRequest createTokenInfoRequest) {

        if (tokenInfoRepository.findByCustomerId(createTokenInfoRequest.customerId()).isEmpty()) {
            removeTokenInfoByCustomerId(createTokenInfoRequest.customerId());
        }

        tokenInfoRepository.save(createTokenInfoRequest.toEntity());

    }

    @Transactional(readOnly = true)
    @Override
    public ReadTokenInfoResponse getByUuid(String uuid) {
        TokenInfo tokenInfo = tokenInfoRepository.findByUuid(uuid).orElseThrow(() -> new ApplicationException(
                ErrorStatus.toErrorStatus("토큰을 찾을 수 없습니다.", 404, LocalDateTime.now())));

        return ReadTokenInfoResponse.fromEntity(tokenInfo);
    }

    @Override
    public boolean isTokenExistByCustomerId(Long customerId) {


        return false;
    }

    @Override
    @Transactional
    public void removeTokenInfoByUuid (String uuid) {
        tokenInfoRepository.deleteByUuid(uuid);
    }

    @Override
    @Transactional
    public void removeTokenInfoByCustomerId (Long customerId) {
        tokenInfoRepository.deleteByCustomerId(customerId);
    }

}