package com.toletter.DTO.letter;

import com.toletter.Entity.Letter;
import com.toletter.Entity.SentBox;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class SaveSentBox {
    @Schema(description = "보내는 유저 이메일", example = "from_test@naver.com")
    private String fromUserEmail;

    @Schema(description = "메일 보낸 시간", example = "2024-03-04T14:02:10")
    private LocalDateTime sentTime;

    @Schema(description = "letter_id(pk)", example = "1")
    private Letter letter;

    public SentBox toEntity() {
        return SentBox.builder()
                .userEmail(fromUserEmail)
                .sentTime(sentTime)
                .letter(letter)
                .build();
    }
}
