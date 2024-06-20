package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.common.provider.JWTUtil;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Object> findUser(@RequestBody LoginUserRequest loginUserRequest) {
        LoginUserResponse user = userService.findUserByEmailAndPassword(loginUserRequest);

        String jwt = jwtUtil.createJwt(
                user.userId(),
                user.userRole(),
                user.loginStatusName()
        );

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwt);

        return ResponseEntity.ok()
                .headers(headers)
                .body(user);
    }

}