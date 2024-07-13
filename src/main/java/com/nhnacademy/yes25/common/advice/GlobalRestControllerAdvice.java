package com.nhnacademy.yes25.common.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;
import com.nhnacademy.yes25.common.exception.ApplicationException;

@RestControllerAdvice
public class GlobalRestControllerAdvice {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorStatus> handleJwtException(ApplicationException e) {
        ErrorStatus errorStatus = e.getErrorStatus();

        return new ResponseEntity<>(errorStatus, errorStatus.toHttpStatus());
    }
}
