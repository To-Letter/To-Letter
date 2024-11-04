package com.toletter.DTO.letter.Response;

import com.toletter.DTO.letter.LetterDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReceivedLetterResponse {
    @Schema(description = "닉네임", example = "test")
    private String user_nickname;

    @Schema(description = "받은 편지 list")
    private List<LetterDTO> listLetter;

    public static ReceivedLetterResponse res(String user_nickname, List<LetterDTO> listLetter) {
        return ReceivedLetterResponse.builder()
                .user_nickname(user_nickname)
                .listLetter(listLetter)
                .build();
    }
}
