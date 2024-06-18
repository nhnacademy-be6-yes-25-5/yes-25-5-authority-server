package com.nhnacademy.yes25.infrastructure.adaptor;

import com.nhnacademy.yes25.presentation.dto.request.LoginUserRequest;
import com.nhnacademy.yes25.presentation.dto.response.LoginUserResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "BOOK-USER-SERVER")
public interface UserAdaptor {

    @PostMapping("/users")
    LoginUserResponse findById(@RequestBody String id);

    @PostMapping("/auth/login")
    LoginUserResponse findByEmailAndPassword(@RequestBody LoginUserRequest loginUserRequest);
}
