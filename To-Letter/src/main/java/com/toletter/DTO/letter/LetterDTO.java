package com.toletter.DTO.letter;

import com.toletter.Entity.Letter;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Column;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class LetterDTO {
    @ApiModelProperty(value = "ID(자동)", example = "1")
    private Long id;

    @ApiModelProperty(value = "받는 유저 닉네임", example = "to_test")
    private String toUserNickname;

    @ApiModelProperty(value = "보내는 유저 닉네임", example = "from_test")
    @Column(nullable = false)
    private String fromUserNickname;

    // 메일 보낸 시간
    @ApiModelProperty(value = "메일 보낸 시간", example = "2024-03-01T06:06:12")
    private LocalDateTime createdAt;

    // 메일 도착할 시간
    @ApiModelProperty(value = "메일 도착할 시간", example = "2024-03-04T06:06:12")
    private LocalDateTime arrivedAt;

    @ApiModelProperty(value = "제목", example = "IT학부즈 보아라.")
    private String title;

    @ApiModelProperty(value = "내용", example = "안녕? 나는 조교야! 우리 친하게 지내자!")
    private String contents;

    @ApiModelProperty(value = "메일 읽었는지 확인", example = "T / F")
    private Boolean viewCheck;

    public static LetterDTO toDTO(Letter letter, String toUserNickname, String fromUserNickname) {
        LetterDTO dto = new LetterDTO();
        dto.setId(letter.getId());
        dto.setToUserNickname(toUserNickname);
        dto.setFromUserNickname(fromUserNickname);
        dto.setCreatedAt(letter.getCreatedAt());
        dto.setArrivedAt(letter.getArrivedAt());
        dto.setTitle(letter.getTitle());
        dto.setContents(letter.getContents());
        dto.setViewCheck(letter.getViewCheck());
        return dto;
    }
}
