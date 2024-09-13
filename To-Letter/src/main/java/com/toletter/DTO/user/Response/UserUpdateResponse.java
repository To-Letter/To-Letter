package com.toletter.DTO.user.Response;

import com.toletter.Entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserUpdateResponse {
    @Schema(description = "상태 코드", example = "200")
    private String responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    @Schema(description = "유저 정보(이메일)")
    private String email;

    @Schema(description = "유저 정보(닉네임)")
    private String nickname;

    @Schema(description = "유저 정보(주소)")
    private String address;

    public static UserUpdateResponse res(String responseCode, String responseMessage, String email, String nickname, String address) {
        return UserUpdateResponse.builder()
                .email(email)
                .nickname(nickname)
                .address(address)
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }
}
