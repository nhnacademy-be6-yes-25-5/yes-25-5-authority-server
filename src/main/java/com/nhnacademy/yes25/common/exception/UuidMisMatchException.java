package com.nhnacademy.yes25.common.exception;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class UuidMisMatchException extends ApplicationException {

    public UuidMisMatchException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
