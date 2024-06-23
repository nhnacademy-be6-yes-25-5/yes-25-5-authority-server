package com.nhnacademy.yes25.infrastructure.adaptor;

import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "userAdaptor", url = "${eureka.gateway}/auth")
public interface UserAdaptor {

    @PostMapping("/users")
    LoginUserResponse findLoginUserByEmailAndPassword(@RequestBody LoginUserRequest loginUserRequest);

}