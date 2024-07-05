package com.nhnacademy.yes25.common.exception;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class MissingJwtHeaderException extends ApplicationException {

    public MissingJwtHeaderException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
