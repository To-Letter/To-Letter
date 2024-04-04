package com.toletter.DTO.email.Request;

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
}
