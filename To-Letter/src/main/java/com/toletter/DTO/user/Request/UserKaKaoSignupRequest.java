package com.toletter.DTO.user.Request;

import com.toletter.Entity.User;
import com.toletter.Enums.LoginType;
import com.toletter.Enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data // get, set 둘 다 됨.
public class UserKaKaoSignupRequest {
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "카카오 회원번호", example = "1122334455")
    private Long kakaoId;

    @Schema(description = "로그인 타입", example = "localLogin/kakaoLogin")
    private LoginType loginType;

    @Schema(description = "2차 인증 확인", example = "T / F")
    private boolean secondConfirmed;

    @Schema(description = "유저 권한", example = "admin / user")
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
