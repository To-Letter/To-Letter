package com.toletter.DTO.user.Request;

import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import com.toletter.Enums.UserRole;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data // get, set 둘 다 됨.
public class UserKaKaoSignupRequest {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "카카오 회원번호")
    private Long kakaoId;

    @ApiModelProperty(value = "로그인 타입", example = "localLogin/kakaoLogin")
    private LoginType loginType;

    @ApiModelProperty(value = "2차 인증 확인", example = "T / F")
    private boolean secondConfirmed;

    @ApiModelProperty(value = "유저 권한", example = "admin / user")
    private UserRole userRole;

    public UserKaKaoSignupRequest(String email, Long kakaoId, LoginType loginType, boolean secondConfirmed, UserRole userRole){
        this.email = email;
        this.kakaoId = kakaoId;
        this.loginType = loginType;
        this.secondConfirmed = secondConfirmed;
        this.userRole = userRole;
    }

    public User toEntity(){
        return User.builder()
                .email(email)
                .kakaoId(kakaoId)
                .loginType(loginType)
                .secondConfirmed(secondConfirmed)
                .userRole(userRole)
                .build();
    }
}
