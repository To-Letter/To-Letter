package com.toletter.DTO.auth.Request;

import com.toletter.Enums.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailVerifyRequest {
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "랜덤코드", example = "qwe123")
    private String randomCode;

    @Schema(description = "메일 인증 타입", example = "secondAuth / updatePW")
    private AuthType authType;
}
