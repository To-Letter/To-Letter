package com.toletter.JWT;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
public class JwtErrorResponse {
    private int errorCode;
    private String message;

    public JwtErrorResponse(HttpStatus status, Throwable ex) {
        this.errorCode = status.value();
        this.message = ex.getMessage();
    }
}
