package com.toletter.DTO.user.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserLoginRequest {
    @Schema(description = "이메일(메일보낼 때 사용)", example = "test@naver.com")
    private String email;

    @Schema(description = "비밀번호", example = "test1234")
    private String password;
}
