package com.nhnacademy.yes25.presentation.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken, String refreshToken){ }
