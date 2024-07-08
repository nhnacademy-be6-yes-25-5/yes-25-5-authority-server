package com.nhnacademy.yes25.presentation.controller;

import com.nhnacademy.yes25.application.service.DormantService;
import com.nhnacademy.yes25.presentation.dto.request.CreateAuthNumberRequest;
import com.nhnacademy.yes25.presentation.dto.request.SubmitAuthNumberRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class DormantController {

    private final DormantService dormantService;

    @PostMapping("/dormant")
    public ResponseEntity<Void> createAuthNumber(@RequestBody CreateAuthNumberRequest request) {
        dormantService.createAuthNumberByEmail(request);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dormant/validate")
    public ResponseEntity<Boolean> submitAuthNumber(@RequestBody SubmitAuthNumberRequest request) {
        return ResponseEntity.ok(dormantService.updateUserStateByAuthNumber(request));
    }
}
