package com.toletter.DTO.auth.Request;

import com.toletter.Entity.Auth;
import com.toletter.Enums.AuthType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailSaveRequest {
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "랜덤코드", example = "qwe123")
    private String randomCode;

    @Schema(description = "메일 구분(2차인증 / 비밀번호 변경)", example = "T/F")
    private AuthType authType;

    public Auth toEntity() {
        return Auth.builder()
                .email(email)
                .randomCode(randomCode)
                .authType(authType)
                .build();
    }
}
