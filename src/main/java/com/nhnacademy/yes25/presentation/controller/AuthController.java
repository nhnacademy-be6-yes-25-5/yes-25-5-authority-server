package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.common.jwt.JwtUserDetails;
import com.nhnacademy.yes25.common.jwt.annotation.CurrentUser;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.presentation.dto.request.CreateTokenInfoRequest;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController 클래스는 사용자 인증 및 로그인 기능을 제공하는 REST API 엔드포인트를 담당하고 있습니다.
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
    private final JWTUtil jwtUtil;

    /**
     * 사용자를 인증하고 인증된 사용자에 대한 JSON Web Token(JWT)을 생성합니다.
     *
     * @param loginUserRequest 사용자의 이메일과 비밀번호를 포함하는 로그인 요청
     * @return 생성된 JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> findLoginUserByEmail(@RequestBody LoginUserRequest loginUserRequest) {

        //회원 정보 가져오기
        LoginUserResponse user = userService.findUserByEmailAndPassword(LoginUserRequest.builder().email(loginUserRequest.email()).password(loginUserRequest.password()).build());

        // 토큰 생성
        String accessJwt = jwtUtil.createAccessJwt();
        String refreshJwt = jwtUtil.createRefrshJwt();

        // DB에 넣는 DTO
        CreateTokenInfoRequest request = CreateTokenInfoRequest.builder()
                .uuid(jwtUtil.getUuidFromToken(accessJwt))
                .customerId(user.userId())
                .role(user.userRole())
                .loginStateName(user.loginStatusName())
                .refreshToken(refreshJwt)
                .build();
        log.info("request: {}", request);

        //DB에 CREATE
        tokenInfoService.createTokenInfo(request);

        //응답 DTO
        AuthResponse response = AuthResponse.builder().accessToken(accessJwt).refreshToken(refreshJwt).build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<String> tokenTest(@CurrentUser JwtUserDetails jwtUserDetails) {
        return ResponseEntity.ok(jwtUserDetails.getUsername());
    }

}