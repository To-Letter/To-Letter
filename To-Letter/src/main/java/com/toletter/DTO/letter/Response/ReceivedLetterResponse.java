package com.toletter.DTO.letter.Response;

import com.toletter.Entity.Letter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReceivedLetterResponse {
    @Schema(description = "아이디")
    private String user_id;

    @Schema(description = "받은 편지 list")
    private List<Letter> listLetter;

    public static ReceivedLetterResponse res(String user_id, List<Letter> listLetter) {
        return ReceivedLetterResponse.builder()
                .user_id(user_id)
                .listLetter(listLetter)
                .build();
    }
}
