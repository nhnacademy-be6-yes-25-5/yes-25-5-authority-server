package com.nhnacademy.yes25.presentation.dto.request;

import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record CreateTokenInfoRequest (
        String uuid,
        Long customerId,
        String role,
        String loginStateName,
        String refreshToken,
        ZonedDateTime createAt,
        ZonedDateTime updateAt
) {
    public TokenInfo toEntity() {
        return TokenInfo.builder()
                .id(null)
                .uuid(uuid)
                .customerId(customerId)
                .role(role)
                .loginStateName(loginStateName)
                .refreshToken(refreshToken)
                .createdAt(createAt)
                .updatedAt(updateAt)
                .build();
    }
}
