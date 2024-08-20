package com.toletter.DTO.user.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDeleteResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    public static UserDeleteResponse res(String responseCode, String responseMessage) {
        return UserDeleteResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }
}
