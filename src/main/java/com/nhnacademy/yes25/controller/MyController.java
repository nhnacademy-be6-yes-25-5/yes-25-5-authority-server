package com.nhnacademy.yes25.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class MyController {
    @GetMapping("/")
    public String hello(HttpServletRequest request) {
        int port = request.getServerPort();
        log.info("port: {}", port);
        return "Hello auth server!";
    }

    @GetMapping("/{userId}")
    public String hello(@PathVariable String userId) {
        return "Hello " + userId;
    }
}
