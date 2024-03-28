package com.toletter.DTO.user.Request;

import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserSignupRequest {
    @ApiModelProperty(value = "아이디(메일보낼 때 사용)")
    private String id;

    @ApiModelProperty(value = "비밀번호(암호화)")
    private String password;

    @ApiModelProperty(value = "닉네임")
    private String nickname;

    @ApiModelProperty(value = "주소", example = "경기도 군포시")
    private String address;

    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "로그인 타입", example = "local/kakao")
    private LoginType loginType;

    public User toEntity(){
        return User.builder()
                .id(id)
                .password(password)
                .nickname(nickname)
                .address(address)
                .email(email)
                .loginType(loginType)
                .build();
    }
}
