package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.presentation.dto.CustomUserDetails;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.CreateTokenResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class LoginServiceImpl {
    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginServiceImpl(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    public CreateTokenResponse login(LoginUserRequest loginUserRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginUserRequest.email(), loginUserRequest.password())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String token = jwtUtil.createJwt(userDetails.getUsername(), userDetails.getAuthorities().iterator().next().getAuthority(), 30 * 60 * 10L);

            return CreateTokenResponse.builder().token(token).build();
        } catch (AuthenticationException e) {
            throw new UnauthorizedAccessExceptionn("ddd");
        }
    }
}