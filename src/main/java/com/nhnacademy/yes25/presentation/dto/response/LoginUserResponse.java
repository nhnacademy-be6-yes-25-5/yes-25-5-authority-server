package com.nhnacademy.yes25.presentation.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponse(String email, String password, String userRole, String loginStatusName){ }
