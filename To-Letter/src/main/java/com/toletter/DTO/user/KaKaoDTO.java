package com.toletter.DTO.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KaKaoDTO {
    @Schema(description = "이메일", example = "test@naver.com")
    private String email;

    @Schema(description = "카카오 회원번호", example = "1122334455")
    private Long userId;

    public KaKaoDTO(String email, Long userId) {
        this.email = email;
        this.userId = userId;
    }
}
