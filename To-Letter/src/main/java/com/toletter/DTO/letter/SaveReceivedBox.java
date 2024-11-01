package com.toletter.DTO.letter;

import com.toletter.Entity.Letter;
import com.toletter.Entity.ReceivedBox;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SaveReceivedBox {
    @Schema(description = "받는 유저 이메일", example = "to_test@naver.com")
    private String toUserEmail;

    @Schema(description = "메일 도착할 시간", example = "2024-03-04T14:02:10")
    private LocalDateTime receivedTime;

    @Schema(description = "letter_id(pk)", example = "1")
    private Letter letter;

    public ReceivedBox toEntity() {
        return ReceivedBox.builder()
                .userEmail(toUserEmail)
                .receivedTime(receivedTime)
                .letter(letter)
                .build();
    }
}
