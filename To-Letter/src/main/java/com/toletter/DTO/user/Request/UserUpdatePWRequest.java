package com.toletter.DTO.user.Request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserUpdatePWRequest {
    @ApiModelProperty(value = "현재 비밀번호")
    private String nowPassword;

    @ApiModelProperty(value = "바뀔 비밀번호")
    private String changePassword;
}
