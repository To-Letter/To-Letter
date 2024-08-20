package com.toletter.Error;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ErrorExceptionHandler {

    @ExceptionHandler(ErrorException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(ErrorException e) {
        return ErrorResponseEntity.toResponseEntity(e.getMessage(), e.getErrorCode());
    }
}
