package com.nhnacademy.yes25.common.exception;


import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class JwtException extends ApplicationException {

    public JwtException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
