package com.nhnacademy.yes25.infrastructure.adaptor;

import com.nhnacademy.yes25.application.service.dto.request.DoorayMessagePayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "doorayAdaptor", url = "${api.dooray}")
public interface DoorayAdaptor {

    @PostMapping
    void sendAuthNumber(@RequestBody DoorayMessagePayload payload);
}
