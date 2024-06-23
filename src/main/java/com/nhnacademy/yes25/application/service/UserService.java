package com.nhnacademy.yes25.application.service;

import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;

public interface UserService {

    LoginUserResponse findUserByEmailAndPassword(LoginUserRequest loginUserRequest);

}
