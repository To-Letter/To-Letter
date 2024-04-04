package com.toletter.DTO.letter.Request;

import com.toletter.Entity.Letter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SendLetterRequest {
    @ApiModelProperty(value = "받는 유저 아이디")
    private String toUserId;

    @ApiModelProperty(value = "메일 도착할 시간")
    private LocalDateTime arrivedAt;

    @ApiModelProperty(value = "제목")
    private String title;

    @ApiModelProperty(value = "내용")
    private String contents;

    public Letter toEntity() {
        return Letter.builder()
                .toUserId(toUserId)
                .arrivedAt(arrivedAt)
                .title(title)
                .contents(contents)
                .build();
    }
}
