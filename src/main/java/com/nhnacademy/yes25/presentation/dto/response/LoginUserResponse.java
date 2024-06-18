package com.nhnacademy.yes25.presentation.dto.response;

public record LoginUserResponse(String email, String password, String userRole, String loginStatusName){ }
