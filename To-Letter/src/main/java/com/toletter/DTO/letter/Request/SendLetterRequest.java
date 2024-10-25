package com.toletter.DTO.letter.Request;

import com.toletter.Entity.Letter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SendLetterRequest {
    @ApiModelProperty(value = "받는 유저 닉네임")
    private String toUserNickname;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String contents;

    @ApiModelProperty(value = "보낸 메시지함에 저장할 지 말지 체크", example = "T/F")
    private boolean saveLetterCheck;

    public Letter toEntity(String toUserEmail) {
        return Letter.builder()
                .toUserEmail(toUserEmail)
                .title(title)
                .contents(contents)
                .build();
    }
}
