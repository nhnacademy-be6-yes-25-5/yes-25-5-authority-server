package com.nhnacademy.yes25.presentation.dto.response;

import lombok.Builder;

@Builder
public record LoginUserResponse(Long userId, String userRole, String loginStatusName){ }
