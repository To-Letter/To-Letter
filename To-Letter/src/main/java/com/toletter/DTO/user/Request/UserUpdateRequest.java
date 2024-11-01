package com.toletter.DTO.user.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data // get, set 둘 다 됨.
@RequiredArgsConstructor
public class UserUpdateRequest {
    @Schema(description = "닉네임", example = "test")
    private String nickname;

    @Schema(description = "주소", example = "경기도 군포시 한세로 30")
    private String address;
}
