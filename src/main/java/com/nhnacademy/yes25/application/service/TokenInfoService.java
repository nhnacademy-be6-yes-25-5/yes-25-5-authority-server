package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user);

    AuthResponse updateAccessToken(CreateAccessTokenRequest request);

    void updateTokenInfo(UpdateTokenInfoRequest updateRequest);

    ReadTokenInfoResponse getByRefreshToken(String refreshToken);

    ReadTokenInfoResponse getByUuid(String uuid);

    ReadTokenInfoResponse getByCustomerId(Long customerId);

    void removeTokenInfoByUuid(String uuid);

    void removeTokenAllInfoByCustomerId(Long customerId);
}