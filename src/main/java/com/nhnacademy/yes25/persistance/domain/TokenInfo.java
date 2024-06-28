package com.nhnacademy.yes25.persistance.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "token_info")
public class TokenInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String uuid;

    @Column(nullable = false, unique = true)
    private Long customerId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private String loginStateName;

    @Column(nullable = false)
    private String refreshToken;

    @Builder
    public TokenInfo(Long id, String uuid, Long customerId, String role, String loginStateName, String refreshToken) {
        this.id = id;
        this.uuid = uuid;
        this.customerId = customerId;
        this.role = role;
        this.loginStateName = loginStateName;
        this.refreshToken = refreshToken;
    }


}