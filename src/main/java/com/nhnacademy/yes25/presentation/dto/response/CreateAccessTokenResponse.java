package com.nhnacademy.yes25.presentation.dto.response;

import lombok.Builder;

@Builder
public record CreateAccessTokenResponse(String accessToken, String refreshToken) { }
