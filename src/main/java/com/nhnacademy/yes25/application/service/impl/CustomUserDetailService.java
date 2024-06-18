package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.infrastructure.adaptor.UserAdaptor;
import com.nhnacademy.yes25.presentation.dto.CustomUserDetails;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserAdaptor userAdaptor;

    public CustomUserDetailService(UserAdaptor userAdaptor) {
        this.userAdaptor = userAdaptor;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        LoginUserResponse userData = userAdaptor.findById(username);

        if (userData != null) {
            return new CustomUserDetails(userData);
        }

        return null;
    }
}
