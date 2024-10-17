package com.toletter.DTO.letter.Request;

import com.toletter.Entity.Letter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SendLetterRequest {
    @ApiModelProperty(value = "받는 유저 닉네임")
    private String toUserNickname;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String contents;

    public Letter toEntity(String toUserEmail) {
        return Letter.builder()
                .toUserEmail(toUserEmail)
                .title(title)
                .contents(contents)
                .build();
    }
}
