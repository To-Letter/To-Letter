package com.toletter.DTO.auth.Request;

import com.toletter.Enums.AuthType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailVerifyRequest {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "랜덤코드")
    private String randomCode;

    @ApiModelProperty(value = "메일 인증 타입")
    private AuthType authType;
}
