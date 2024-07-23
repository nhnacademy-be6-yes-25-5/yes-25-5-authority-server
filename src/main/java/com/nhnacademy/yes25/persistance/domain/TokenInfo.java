package com.nhnacademy.yes25.persistance.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "token")
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String uuid;

    @Column(nullable = false, name = "refresh_token")
    private String refreshToken;

    @Column(nullable = false, name = "expiry_date")
    private LocalDateTime expiryDate;

    @Builder
    public TokenInfo(Long id, String uuid, String refreshToken, LocalDateTime expiryDate) {
        this.id = id;
        this.uuid = uuid;
        this.refreshToken = refreshToken;
        this.expiryDate = expiryDate;
    }
}