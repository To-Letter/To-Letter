package com.toletter.DTO.user.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserUpdatePWRequest {
    @Schema(description = "현재 비밀번호", example = "test1234")
    private String nowPassword;

    @Schema(description = "바뀔 비밀번호", example = "test123")
    private String changePassword;
}
