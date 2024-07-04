package com.nhnacademy.yes25.presentation.dto.response;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import lombok.Builder;

@Builder
public record NoneMemberLoginResponse(
        String accessToken,
        String refreshToken,
        Long customerId,
        String role
)
{
    public static NoneMemberLoginResponse fromEntity(TokenInfo tokenInfo, String accessToken) {
        return NoneMemberLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(tokenInfo.getRefreshToken())
                .customerId(tokenInfo.getCustomerId())
                .role(tokenInfo.getRole())
                .build();
    }
}
