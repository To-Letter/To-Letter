package com.toletter.DTO.user.Response;

import com.toletter.Enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class UserUpdateResponse {
    @Schema(description = "유저 정보(이메일)")
    private String email;

    @Schema(description = "유저 정보(닉네임)")
    private String nickname;

    @Schema(description = "유저 정보(주소)")
    private String address;

    @Schema(description = "로그인 타입", example = "localLogin/kakaoLogin")
    private LoginType loginType;

    public static UserUpdateResponse res(String email, String nickname, String address, LoginType loginType) {
        return UserUpdateResponse.builder()
                .email(email)
                .nickname(nickname)
                .address(address)
                .loginType(loginType)
                .build();
    }
}
