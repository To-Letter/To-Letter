package com.toletter.DTO.auth.Request;

import com.toletter.Entity.Auth;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class EmailSaveRequest {
    @ApiModelProperty(value = "이메일")
    private String email;

    @ApiModelProperty(value = "랜덤코드")
    private String randomCode;

    public Auth toEntity() {
        return Auth.builder()
                .email(email)
                .randomCode(randomCode)
                .build();
    }
}
