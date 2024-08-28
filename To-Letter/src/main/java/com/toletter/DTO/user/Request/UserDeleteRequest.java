package com.toletter.DTO.user.Request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserDeleteRequest {
    @ApiModelProperty(value = "아이디(메일보낼 때 사용)")
    private String id;

    @ApiModelProperty(value = "비밀번호")
    private String password;
}
