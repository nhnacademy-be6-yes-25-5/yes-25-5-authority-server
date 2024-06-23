package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import lombok.RequiredArgsConstructor;
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
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    /**
     * 사용자를 인증하고 인증된 사용자에 대한 JSON Web Token(JWT)을 생성합니다.
     *
     * @param loginUserRequest 사용자의 이메일과 비밀번호를 포함하는 로그인 요청
     * @return 생성된 JWT 토큰
     */
    @PostMapping("/login")
    public ResponseEntity<String>  findUser(@RequestBody LoginUserRequest loginUserRequest) {
        LoginUserResponse user = userService.findUserByEmailAndPassword(loginUserRequest);

        String jwt = jwtUtil.createJwt(
                user.userId(),
                user.userRole(),
                user.loginStatusName()
        );

        return ResponseEntity.ok(jwt);
    }
}