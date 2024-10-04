package com.toletter.DTO.user.Response;

import com.toletter.DTO.user.KaKaoDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserKaKaoLoginResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    @Schema(description = "유저 정보", example = "이메일 등")
    private KaKaoDTO userInfo;

    public static UserKaKaoLoginResponse res(String responseCode, String responseMessage, KaKaoDTO userInfo) {
        return UserKaKaoLoginResponse.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .userInfo(userInfo)
                .build();
    }
}
