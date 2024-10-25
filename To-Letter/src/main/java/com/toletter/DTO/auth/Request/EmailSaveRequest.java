package com.toletter.DTO.auth.Request;

import com.toletter.Entity.Auth;
import com.toletter.Enums.AuthType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailSaveRequest {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "랜덤코드")
    private String randomCode;

    @ApiModelProperty(value = "메일 구분(2차인증 / 비밀번호 변경)")
    private AuthType authType;

    public Auth toEntity() {
        return Auth.builder()
                .email(email)
                .randomCode(randomCode)
                .authType(authType)
                .build();
    }
}
