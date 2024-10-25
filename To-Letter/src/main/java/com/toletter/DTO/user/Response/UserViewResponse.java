package com.toletter.DTO.user.Response;

import com.toletter.Enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserViewResponse {
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "이메일")
    private String email;

    @Schema(description = "주소")
    private String address;

    @Schema(description = "로그인 타입", example = "localLogin/kakaoLogin")
    private LoginType loginType;

    public static UserViewResponse res(String address, String nickname, String email, LoginType loginType) {
        return UserViewResponse.builder()
                .nickname(nickname)
                .email(email)
                .address(address)
                .loginType(loginType)
                .build();
    }
}
