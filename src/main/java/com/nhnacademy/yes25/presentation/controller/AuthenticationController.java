//package com.nhnacademy.yes25.presentation.controller;
//
//import com.nhnacademy.yes25.application.service.impl.LoginServiceImpl;
//import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthenticationController {
//    private final LoginServiceImpl loginService;
//
//    public AuthenticationController(LoginServiceImpl loginService) {
//        this.loginService = loginService;
//    }
//
//    @PostMapping("/login")
//    public ResponseEntity<Void> login(@RequestBody LoginUserRequest loginUserRequest) {
//        String token = (loginService.login(loginUserRequest)).token();
//        return ResponseEntity.ok()
//                .header("Authorization", "Bearer " + token)
//                .build();
//    }
//}