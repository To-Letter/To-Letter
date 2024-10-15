package com.toletter.Error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ErrorResponseEntity {
    private int responseCode;
    private String responseStatus;
    private String responseMessage;

    public static ResponseEntity<ErrorResponseEntity> toResponseEntity(int responseCode ,String message, ErrorCode e){
        return ResponseEntity
                .status(responseCode)
                .body(ErrorResponseEntity.builder()
                        .responseCode(e.getStatus().value())
                        .responseStatus(e.name())
                        .responseMessage(message)
                        .build()
                );
    }
}
