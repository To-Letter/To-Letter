package com.toletter.DTO.letter;

import com.toletter.Document.LetterDocument;
import com.toletter.Entity.Letter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class LetterDTO {
    @Schema(description = "ID(자동)", example = "1")
    private Long id;

    @Schema(description = "받는 유저 닉네임", example = "to_test")
    private String toUserNickname;

    @Schema(description = "보내는 유저 닉네임", example = "from_test")
    @Column(nullable = false)
    private String fromUserNickname;

    // 메일 보낸 시간
    @Schema(description = "메일 보낸 시간", example = "2024-03-01T06:06:12")
    private LocalDateTime createdAt;

    // 메일 도착할 시간
    @Schema(description = "메일 도착할 시간", example = "2024-03-04T06:06:12")
    private LocalDateTime arrivedAt;

    @Schema(description = "내용", example = "안녕? 나는 조교야! 우리 친하게 지내자!")
    private String contents;

    @Schema(description = "메일 읽었는지 확인", example = "T / F")
    private Boolean viewCheck;

    public static LetterDTO toDTO(Letter letter, String contents) {
        LetterDTO dto = new LetterDTO();
        dto.setId(letter.getId());
        dto.setToUserNickname(letter.getToUserNickname());
        dto.setFromUserNickname(letter.getFromUserNickname());
        dto.setCreatedAt(letter.getCreatedAt());
        dto.setArrivedAt(letter.getArrivedAt());
        dto.setContents(contents);
        dto.setViewCheck(letter.getViewCheck());
        return dto;
    }

    public static LetterDTO toDocumentDTO(LetterDocument letterDocument){
        LetterDTO dto = new LetterDTO();
        dto.setId(letterDocument.getId());
        dto.setToUserNickname(letterDocument.getToUserNickname());
        dto.setFromUserNickname(letterDocument.getFromUserNickname());
        dto.setCreatedAt(letterDocument.getCreatedAt());
        dto.setArrivedAt(letterDocument.getArrivedAt());
        dto.setContents(letterDocument.getContent());
        dto.setViewCheck(letterDocument.getViewCheck());
        return dto;
    }
}
