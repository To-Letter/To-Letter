package com.toletter.DTO.user.Request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserLoginRequest {
    @ApiModelProperty(value = "이메일(메일보낼 때 사용)")
    private String email;

    @ApiModelProperty(value = "비밀번호")
    private String password;
}
