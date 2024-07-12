package com.nhnacademy.yes25.service;

import com.nhnacademy.yes25.application.service.impl.UserServiceImpl;
import com.nhnacademy.yes25.infrastructure.adaptor.UserAdaptor;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserAdaptor userAdaptor;

    @InjectMocks
    private UserServiceImpl userService;

    private LoginUserRequest loginUserRequest;
    private LoginUserResponse loginUserResponse;

    @BeforeEach
    void setUp() {
        loginUserRequest = LoginUserRequest.builder()
                .email("test@example.com")
                .password("password")
                .build();

        loginUserResponse = LoginUserResponse.builder()
                .userId(1L)
                .userRole("ROLE_USER")
                .loginStatusName("ACTIVE")
                .build();
    }

    @DisplayName("이메일과 비밀번호로 로그인 사용자 조회 - 성공")
    @Test
    void findUserByEmailAndPassword_success() {
        // given
        when(userAdaptor.findLoginUserByEmailAndPassword(loginUserRequest))
                .thenReturn(ResponseEntity.ok(loginUserResponse));

        // when
        LoginUserResponse result = userService.findUserByEmailAndPassword(loginUserRequest);

        // then
        assertEquals(loginUserResponse.userId(), result.userId());
        assertEquals(loginUserResponse.userRole(), result.userRole());
        assertEquals(loginUserResponse.loginStatusName(), result.loginStatusName());
        verify(userAdaptor, times(1)).findLoginUserByEmailAndPassword(loginUserRequest);
    }
}