package com.nhnacademy.yes25.presentation.dto.response;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import lombok.Builder;

@Builder
public record ReadTokenInfoResponse (
        Long customerId,
        String role,
        String loginStateName,
        String refreshJwt
)
{
    public static ReadTokenInfoResponse fromEntity(TokenInfo tokenInfo) {
        return ReadTokenInfoResponse.builder()
                .customerId(tokenInfo.getCustomerId())
                .role(tokenInfo.getRole())
                .loginStateName(tokenInfo.getLoginStateName())
                .refreshJwt(tokenInfo.getRefreshToken())
                .build();
    }
}
