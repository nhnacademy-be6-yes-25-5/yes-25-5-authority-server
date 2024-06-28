package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;

public interface TokenInfoService {

    void createTokenInfo(CreateTokenInfoRequest createTokenInfoRequest);

    ReadTokenInfoResponse getByUuid(String uuid);

    boolean isTokenExistByCustomerId(Long customerId);

    void removeTokenInfoByUuid(String uuid);

    void removeTokenInfoByCustomerId(Long customerId);
}