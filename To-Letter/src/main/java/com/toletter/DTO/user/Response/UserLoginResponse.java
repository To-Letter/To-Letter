package com.toletter.DTO.user.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
public class UserLoginResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    public static UserLoginResponse res(String responseCode, String responseMessage) {
        return UserLoginResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }
}
