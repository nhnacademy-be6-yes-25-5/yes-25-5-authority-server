package com.nhnacademy.yes25.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.yes25.application.service.TokenInfoService;
import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.request.NoneMemberLoginRequest;
import com.nhnacademy.yes25.presentation.dto.response.AuthResponse;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import com.nhnacademy.yes25.presentation.dto.response.NoneMemberLoginResponse;
import com.nhnacademy.yes25.presentation.dto.response.ReadTokenInfoResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.nhnacademy.yes25.presentation.controller.AuthController;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private TokenInfoService tokenInfoService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void findLoginUserByEmail_ShouldReturnAuthResponse() throws Exception {
        LoginUserRequest loginRequest = new LoginUserRequest("test@example.com", "password");
        LoginUserResponse userResponse = new LoginUserResponse(1L,  "ROLE_USER", "ACTIVE");
        AuthResponse authResponse = new AuthResponse("accessToken", "refreshToken");

        when(userService.findUserByEmailAndPassword(any(LoginUserRequest.class))).thenReturn(userResponse);
        when(tokenInfoService.doLogin(any(LoginUserResponse.class))).thenReturn(authResponse);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer accessToken"))
                .andExpect(header().string("Refresh-Token", "refreshToken"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void findLoginUserByEmail_NoneMember_ShouldReturnNoneMemberLoginResponse() throws Exception {
        NoneMemberLoginRequest request = new NoneMemberLoginRequest(2L,  "ROLE_NONE_MEMBER");
        NoneMemberLoginResponse response = new NoneMemberLoginResponse("accessToken", "refreshToken", 2L, "ROLE_NONE_MEMBER");

        when(tokenInfoService.doLoginNoneMember(any(NoneMemberLoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login/none")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer accessToken"))
                .andExpect(header().string("Refresh-Token", "refreshToken"))
                .andExpect(jsonPath("$.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.refreshToken").value("refreshToken"));
    }

    @Test
    void tokenRefresh_ShouldReturnNewAuthResponse() throws Exception {
        AuthResponse authResponse = new AuthResponse("newAccessToken", "newRefreshToken");

        when(tokenInfoService.updateAccessToken(any())).thenReturn(authResponse);

        mockMvc.perform(get("/auth/refresh")
                        .header("Refresh-Token", "oldRefreshToken"))
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", "Bearer newAccessToken"))
                .andExpect(header().string("Refresh-Token", "newRefreshToken"))
                .andExpect(jsonPath("$.accessToken").value("newAccessToken"))
                .andExpect(jsonPath("$.refreshToken").value("newRefreshToken"));
    }

    @Test
    void getUserInfo_ShouldReturnReadTokenInfoResponse() throws Exception {
        String uuid = "test-uuid";
        ReadTokenInfoResponse response = ReadTokenInfoResponse.builder()
                .customerId(1L)
                .role("ROLE_USER")
                .loginStateName("ACTIVE")
                .refreshJwt("refresh-token-value")
                .build();

        when(tokenInfoService.getByUuid(uuid)).thenReturn(response);

        mockMvc.perform(get("/auth/info")
                        .param("uuid", uuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerId").value(1))
                .andExpect(jsonPath("$.role").value("ROLE_USER"))
                .andExpect(jsonPath("$.loginStateName").value("ACTIVE"))
                .andExpect(jsonPath("$.refreshJwt").value("refresh-token-value"));
    }
}