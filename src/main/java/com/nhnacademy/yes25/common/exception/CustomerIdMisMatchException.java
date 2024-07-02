package com.nhnacademy.yes25.common.exception;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class CustomerIdMisMatchException extends ApplicationException {

    public CustomerIdMisMatchException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
