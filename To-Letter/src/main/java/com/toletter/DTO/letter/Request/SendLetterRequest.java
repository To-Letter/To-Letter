package com.toletter.DTO.letter.Request;

import com.toletter.Entity.Letter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SendLetterRequest {
    @Schema(description = "받는 유저 닉네임", example = "test")
    private String toUserNickname;

    @Schema(description = "내용", example = "안녕? 우리 친하게 지내자!")
    private String contents;

    @Schema(description = "보낸 메시지함에 저장할 지 말지 체크", example = "T/F")
    private boolean saveLetterCheck;

    public Letter toEntity(String toUserEmail) {
        return Letter.builder()
                .toUserEmail(toUserEmail)
                .contents(contents)
                .build();
    }
}
