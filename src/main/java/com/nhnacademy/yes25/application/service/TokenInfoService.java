package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user);

    void createTokenInfo(LoginUserResponse user, String accessJwt, String refreshJwt);

    AuthResponse updateAccessToken(CreateAccessTokenRequest request);

    ReadTokenInfoResponse getByUuid(String uuid);

    ReadTokenInfoResponse getByCustomerId(Long customerId);

    void updateTokenInfo(UpdateTokenInfoRequest updateRequest);

    void removeTokenInfoByUuid(String uuid);

    void removeTokenAllInfoByCustomerId(Long customerId);
}