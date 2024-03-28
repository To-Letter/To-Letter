package com.toletter.DTO.user.Response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserViewResponse {
    @Schema(description = "아이디")
    private String id;

    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "이메일")
    private String email;

    public static UserViewResponse res(String id, String nickname, String email) {
        return UserViewResponse.builder()
                .id(id)
                .nickname(nickname)
                .email(email)
                .build();
    }
}
