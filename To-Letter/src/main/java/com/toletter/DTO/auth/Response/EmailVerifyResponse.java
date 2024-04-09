package com.toletter.DTO.auth.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
public class EmailVerifyResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    public static EmailVerifyResponse res(String responseCode, String responseMessage) {
        return EmailVerifyResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }
}
