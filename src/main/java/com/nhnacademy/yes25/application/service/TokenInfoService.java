package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.common.exception.ApplicationException;
import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user) throws ApplicationException;

    void createTokenInfo(CreateTokenInfoRequest createTokenInfoRequest);

    ReadTokenInfoResponse getByUuid(String uuid);

    ReadTokenInfoResponse getByCustomerId(Long customerId);

    void updateTokenInfo(UpdateTokenInfoRequest updateTokenInfoRequest);

    void removeTokenInfoByUuid(String uuid);

    void removeTokenAllInfoByCustomerId(Long customerId);
}