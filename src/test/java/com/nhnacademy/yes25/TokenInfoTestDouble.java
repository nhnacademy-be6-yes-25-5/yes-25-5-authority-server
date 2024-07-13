package com.nhnacademy.yes25;

import java.time.ZonedDateTime;
import com.nhnacademy.yes25.persistance.domain.TokenInfo;
import lombok.Setter;

@Setter
public class TokenInfoTestDouble extends TokenInfo {
    public TokenInfoTestDouble(Long id, String uuid, Long customerId, String role,
                               String loginStateName, String refreshToken,
                               ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        super();

    }
}