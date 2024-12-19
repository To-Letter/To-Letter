package com.toletter.Error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class ErrorExceptionHandler {

    // 전역 에러 처리(내마음대로)
    @ExceptionHandler(ErrorException.class)
    protected ResponseEntity<ErrorResponseEntity> handleCustomException(ErrorException e) {
        return ErrorResponseEntity.toResponseEntity(e.getResponseCode(), e.getMessage(), e.getErrorCode());
    }

    // 그 외 모든 에러처리(보통 500 에러 처리)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseEntity> handleAllExceptions(Exception ex) {
        log.error(String.valueOf(ex));
        return ErrorResponseEntity.toResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage(), ErrorCode.INTERNAL_SERVER_EXCEPTION);
    }
}