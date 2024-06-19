package com.nhnacademy.yes25.application.service.impl;

public class UnauthorizedAccessExceptionn extends RuntimeException {
    public UnauthorizedAccessExceptionn(String message) {
        super(message);
    }
}
