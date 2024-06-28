package com.nhnacademy.yes25.persistance.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.ZonedDateTime;

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

    @Column(nullable = false, name = "create_at")
    private ZonedDateTime createdAt;

    @Column(nullable = false, name = "update_at")
    private ZonedDateTime updatedAt;

    @Builder
    public TokenInfo(Long id, String uuid, Long customerId, String role,
                     String loginStateName, String refreshToken,
                     ZonedDateTime createdAt, ZonedDateTime updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.customerId = customerId;
        this.role = role;
        this.loginStateName = loginStateName;
        this.refreshToken = refreshToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

}