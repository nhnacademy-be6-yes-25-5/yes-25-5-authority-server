package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.common.jwt.JwtUserDetails;
import com.nhnacademy.yes25.common.jwt.annotation.CurrentUser;
import com.nhnacademy.yes25.presentation.dto.request.NoneMemberLoginRequest;
import com.nhnacademy.yes25.presentation.dto.request.CreateAccessTokenRequest;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.NoneMemberLoginResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * AuthController 클래스는 사용자 인증 및 로그인 기능을 제공하는 REST API 엔드포인트를 담당합니다.
 * 로그인, 토큰 테스트, 토큰 갱신 등의 기능을 제공합니다.
 *
 * @author lettuce82
 * @version 1.0
 */
@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;
    private final TokenInfoService tokenInfoService;

    /**
     * 사용자를 인증하고 인증된 사용자에 대한 JSON Web Token(JWT)을 생성합니다.
     *
     * @param loginUserRequest 사용자의 이메일과 비밀번호를 포함하는 로그인 요청
     * @return ResponseEntity<AuthResponse> 생성된 JWT 토큰을 포함한 응답
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> findLoginUserByEmail(@RequestBody LoginUserRequest loginUserRequest) {
        LoginUserResponse user = userService.findUserByEmailAndPassword(LoginUserRequest.builder()
                .email(loginUserRequest.email())
                .password(loginUserRequest.password())
                .build());

        AuthResponse authResponse = tokenInfoService.doLogin(user);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header("Refresh-Token", authResponse.refreshToken())
                .body(authResponse);
    }

    @PostMapping("/login/none")
    public ResponseEntity<NoneMemberLoginResponse> findLoginUserByEmail(@RequestBody NoneMemberLoginRequest request) {
        NoneMemberLoginResponse authResponse = tokenInfoService.doLoginNoneMember(request);

        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header("Refresh-Token", authResponse.refreshToken())
                .body(authResponse);
    }

    /**
     * 토큰의 유효성을 테스트합니다.
     * 현재 인증된 사용자의 정보를 반환합니다.
     *
     * @param jwtUserDetails 현재 인증된 사용자의 상세 정보
     * @return ResponseEntity<String> 인증된 사용자의 username
     */
    @GetMapping("/test")
    public ResponseEntity<String> tokenTest(@CurrentUser JwtUserDetails jwtUserDetails) {
        return ResponseEntity.ok(jwtUserDetails.getUsername());
    }

    /**
     * 만료된 액세스 토큰을 갱신합니다.
     *
     * @param expiredAccessJwt
     * @return ResponseEntity<AuthResponse> 새로 발급된 액세스 토큰과 리프레시 토큰을 포함한 응답
     */
    @GetMapping("/refresh")
    public ResponseEntity<AuthResponse> tokenRefresh(@RequestHeader("Authorization") String expiredAccessJwt) {

        expiredAccessJwt = expiredAccessJwt.replace("Bearer ", "");
        CreateAccessTokenRequest createAccessTokenRequest = new CreateAccessTokenRequest(expiredAccessJwt);
        AuthResponse authResponse = tokenInfoService.updateAccessToken(createAccessTokenRequest);
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + authResponse.accessToken())
                .header("Refresh-Token", authResponse.refreshToken())
                .body(authResponse);
    }
}