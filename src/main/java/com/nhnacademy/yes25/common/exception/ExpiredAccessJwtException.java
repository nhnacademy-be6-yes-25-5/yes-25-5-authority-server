package com.nhnacademy.yes25.common.exception;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class ExpiredAccessJwtException extends ApplicationException {

    public ExpiredAccessJwtException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
