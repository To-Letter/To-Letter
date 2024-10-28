package com.toletter.DTO.user.Request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserFindUpdatePWRequest {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "바뀔 비밀번호")
    private String changePassword;
}
