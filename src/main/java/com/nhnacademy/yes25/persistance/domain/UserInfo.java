package com.nhnacademy.yes25.persistance.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "user_info")
public class UserInfo {
    @Id
    private String uuid;

    @Column(nullable = false, unique = true, name = "customer_id")
    private Long customerId;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false, name = "login_state_name")
    private String loginStateName;

    @Builder
    public UserInfo(String uuid, Long customerId, String role, String loginStateName) {
        this.uuid = uuid;
        this.customerId = customerId;
        this.role = role;
        this.loginStateName = loginStateName;
    }
}
