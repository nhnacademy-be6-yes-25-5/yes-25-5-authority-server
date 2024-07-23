package com.nhnacademy.yes25.presentation.dto.response;

import com.nhnacademy.yes25.persistance.domain.UserInfo;
import lombok.Builder;

@Builder
public record ReadTokenInfoResponse (
        Long customerId,
        String role,
        String loginStateName,
        String refreshJwt
)
{
    public static ReadTokenInfoResponse fromEntity(UserInfo userInfo, String refreshJwt) {
        return ReadTokenInfoResponse.builder()
                .customerId(userInfo.getCustomerId())
                .role(userInfo.getRole())
                .loginStateName(userInfo.getLoginStateName())
                .refreshJwt(refreshJwt)
                .build();
    }
}
