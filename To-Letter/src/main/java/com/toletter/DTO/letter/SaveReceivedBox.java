package com.toletter.DTO.letter;

import com.toletter.Entity.Letter;
import com.toletter.Entity.ReceivedBox;
import com.toletter.Entity.SentBox;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SaveReceivedBox {
    @ApiModelProperty(value = "받는 유저 닉네임")
    private String toUserNickname;

    @ApiModelProperty(value = "메일 도착할 시간", example = "2024-03-04T14:02:10")
    private LocalDateTime receivedTime;

    @ApiModelProperty(value = "letter_id(pk)", example = "1")
    private Letter letter;

    public ReceivedBox toEntity() {
        return ReceivedBox.builder()
                .user_nickname(toUserNickname)
                .receivedTime(receivedTime)
                .letter(letter)
                .build();
    }
}
