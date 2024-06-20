package com.nhnacademy.yes25.application.service.impl;

import com.nhnacademy.yes25.application.service.UserService;
import com.nhnacademy.yes25.infrastructure.adaptor.UserAdaptor;
import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private  final UserAdaptor userAdaptor;

    @Override
    public LoginUserResponse findUserByEmailAndPassword(LoginUserRequest loginUserRequest) {
        return userAdaptor.findLoginUserByEmailAndPassword(loginUserRequest);
    }

}
