package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.springframework.transaction.annotation.Transactional;

public interface TokenInfoService {

    AuthResponse doLogin(LoginUserResponse user);

    void deleteExistingUserData(Long userId);

    @Transactional(readOnly = true)
    boolean isRefreshTokenValid(String refreshToken);

    AuthResponse updateAccessToken(String refreshToken,
                                   LoginUserResponse user);
}