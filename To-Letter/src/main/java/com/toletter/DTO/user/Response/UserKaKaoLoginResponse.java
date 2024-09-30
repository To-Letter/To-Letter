package com.toletter.DTO.user.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class UserKaKaoLoginResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    @Schema(description = "유저 정보", example = "이메일 등")
    private Map userInfo;

    public static UserKaKaoLoginResponse res(String responseCode, String responseMessage, Map userInfo) {
        return UserKaKaoLoginResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .userInfo(userInfo)
                .build();
    }
}
