package com.toletter.Error;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
    private final int responseCode;
    private final ErrorCode errorCode;

    public ErrorException (String message, int responseCode, ErrorCode errorCode){
        super(message);
        this.responseCode = responseCode;
        this.errorCode = errorCode;
    }
}
