package com.nhnacademy.yes25.common.aop;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.nhnacademy.yes25.common.exception.ApplicationException;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalControllerAdvice {

    @ControllerAdvice
    public static class GlobalExceptionHandler {

        @ExceptionHandler(ApplicationException.class)
        public ResponseEntity<ErrorStatus> handleException(ApplicationException e) {

            ErrorStatus errorStatus = e.getErrorStatus();

            return new ResponseEntity<>(errorStatus, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
