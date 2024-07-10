package com.nhnacademy.yes25.common.exception;

import com.nhnacademy.yes25.common.exception.payload.ErrorStatus;

public class refreshTokenMisMatchException extends ApplicationException {

    public refreshTokenMisMatchException(ErrorStatus errorStatus) {
        super(errorStatus);
    }

}
