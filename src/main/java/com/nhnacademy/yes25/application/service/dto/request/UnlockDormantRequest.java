package com.nhnacademy.yes25.application.service.dto.request;

import lombok.Builder;

@Builder
public record UnlockDormantRequest(String email) {

    public static UnlockDormantRequest from(String email) {
        return UnlockDormantRequest.builder()
            .email(email)
            .build();
    }
}
