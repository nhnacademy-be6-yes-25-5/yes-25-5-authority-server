package com.nhnacademy.yes25.presentation.dto.request;

import lombok.Builder;

@Builder
public record LoginUserRequest(String email) { }
