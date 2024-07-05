package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.NoneMemberLoginRequest;
import com.nhnacademy.yes25.presentation.dto.request.UpdateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.NoneMemberLoginResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import org.springframework.transaction.annotation.Transactional;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user);

    NoneMemberLoginResponse doLoginNoneMember(NoneMemberLoginRequest request);

    void createTokenInfo(LoginUserResponse user, String accessJwt, String refreshJwt);

    AuthResponse updateAccessToken(CreateAccessTokenRequest request);

    @Transactional
    void createTokenInfoNoneMember(NoneMemberLoginRequest request, String accessJwt, String refreshJwt);

    ReadTokenInfoResponse getByUuid(String uuid);

    ReadTokenInfoResponse getByCustomerId(Long customerId);

    void updateTokenInfo(UpdateTokenInfoRequest updateRequest);

    void removeTokenInfoByUuid(String uuid);

    void removeTokenAllInfoByCustomerId(Long customerId);
}