package com.toletter.Error;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException {
    private final ErrorCode errorCode;

    public ErrorException (String message, ErrorCode errorCode){
        super(message);
        this.errorCode = errorCode;
    }
}
