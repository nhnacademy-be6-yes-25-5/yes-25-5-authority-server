package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.common.jwt.JwtProvider;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController 클래스는 사용자 인증 및 로그인 기능을 제공하는 REST API 엔드포인트를 담당합니다.
 * 로그인, 토큰 갱신, 회원 정보 조회 등의 기능을 제공합니다.
 *
 * @author Chaesanghui
 * @version 1.0
 */
@Tag(name = "회원 인증 API", description = "회원 인증 관련 API 입니다.")
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final TokenInfoService tokenInfoService;
    private final JwtProvider jwtProvider;

    /**
     * 사용자를 인증하고 인증된 사용자에 대한 JSON Web Token(JWT)을 생성합니다.
     *
     * @param loginUserRequest 사용자의 이메일과 비밀번호를 포함하는 로그인 요청
     * @param response HTTP 응답 객체
     * @return ResponseEntity<AuthResponse> 생성된 JWT 토큰을 포함한 응답
     */
    @Operation(summary = "회원 로그인", description = "로그인 진행 후 Header에 토큰을 담아 반환합니다.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> findLoginUserByEmail(@RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {

        LoginUserResponse user = userService.findUserByEmailAndPassword(LoginUserRequest.builder()
                .email(loginUserRequest.email())
                .password(loginUserRequest.password())
                .build());


        AuthResponse authResponse = tokenInfoService.doLogin(user);

        response.setHeader("Authorization", "Bearer " + authResponse.accessToken());
        response.setHeader("Refresh-Token", authResponse.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header("Refresh-Token", authResponse.refreshToken())
                .body(authResponse);
    }

    /**
     * 만료된 액세스 토큰을 갱신합니다.
     *
     * @param refreshToken 리프레시 토큰
     * @return ResponseEntity<AuthResponse> 새로 발급된 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @Operation(summary = "access token 재발급", description = "access token 재발급 후 Header에 재발급된 토큰을 담아 반환합니다.")
    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> tokenRefresh(@RequestHeader(HttpHeaders.AUTHORIZATION) String accessHeader,
                                                     @RequestHeader("Refresh-Token") String refreshToken) {

        String accessToken = jwtProvider.extractTokenFromHeader(accessHeader);
        LoginUserResponse user = jwtProvider.getLoginUserFromToken(accessToken);
        AuthResponse authResponse = tokenInfoService.updateAccessToken(refreshToken, user);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header("Refresh-Token", authResponse.refreshToken())
                .body(authResponse);
    }

}