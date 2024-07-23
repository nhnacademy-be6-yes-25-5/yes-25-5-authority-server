package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import com.nhnacademy.yes25.persistance.domain.UserInfo;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user);

    void deleteExistingUserData(Long customerId);

    void createTokenInfo(String uuid, String refreshJwt);

    void createUserInfo(LoginUserResponse user, String uuid);

    ReadTokenInfoResponse getByUuid(String uuid);

    AuthResponse updateAccessToken(CreateAccessTokenRequest createAccessTokenRequest);

    TokenInfo getTokenInfoByUuid(String uuid);

    UserInfo getUserInfoByUuid(String uuid);

    TokenInfo getTokenInfoByRefreshToken(String refreshToken);

    void updateTokenInfo(TokenInfo oldTokenInfo, String newUuid, String newRefreshJwt);

    void updateUserInfo(String oldUuid, String newUuid);

    void cleanupExpiredTokens();

}