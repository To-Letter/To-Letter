package com.toletter.Error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private int status;
    private String code;
    private String message;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(String message, ErrorCode e){
        return ResponseEntity
                .status(e.getStatus())
                .body(ErrorResponseEntity.builder()
                        .status(e.getStatus().value())
                        .code(e.name())
                        .message(message)
                        .build()
                );
    }
}
