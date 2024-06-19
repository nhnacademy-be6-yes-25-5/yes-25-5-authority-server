package com.nhnacademy.yes25.presentation.dto;

import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final LoginUserResponse loginUserResponse;

    public CustomUserDetails(LoginUserResponse loginUserResponse) {
        this.loginUserResponse = loginUserResponse;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new GrantedAuthority() {

            @Override
            public String getAuthority() {
                return loginUserResponse.userRole();
            }
        });

        return authorities;
    }

    @Override
    public String getPassword() {

        return loginUserResponse.password();
    }

    @Override
    public String getUsername() {

        return loginUserResponse.email();
    }

    @Override
    public boolean isAccountNonExpired() {

        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {

        String status = loginUserResponse.loginStatusName();

        return !"휴면".equals(status);
    }

    @Override
    public boolean isCredentialsNonExpired() {

        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {

        String status = loginUserResponse.loginStatusName();

        return "active".equalsIgnoreCase(status);
    }
}