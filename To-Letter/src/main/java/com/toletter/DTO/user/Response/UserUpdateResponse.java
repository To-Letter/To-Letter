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

    @Schema(description = "유저 정보")
    private User user;

    public static UserUpdateResponse res(String responseCode, String responseMessage, User user) {
        return UserUpdateResponse.builder()
                .user(user)
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .build();
    }
}
