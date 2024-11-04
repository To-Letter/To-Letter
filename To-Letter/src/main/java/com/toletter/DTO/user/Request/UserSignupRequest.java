package com.toletter.DTO.user.Request;

import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserSignupRequest {
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "비밀번호", example = "test1234")
    private String password;

    @Schema(description = "닉네임", example = "test")
    private String nickname;

    @Schema(description = "주소", example = "경기도 군포시")
    private String address;

    @Schema(description = "로그인 타입", example = "localLogin/kakaoLogin")
    private LoginType loginType;

    public User toEntity(){
        return User.builder()
                .password(password)
                .nickname(nickname)
                .address(address)
                .email(email)
                .loginType(loginType)
                .build();
    }
}
