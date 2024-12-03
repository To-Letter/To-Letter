package com.toletter.DTO.letter.Request;

import com.toletter.Enums.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class SearchLetterRequest {
    @Schema(description = "검색할 letter 종류", example = "receivedLetter / sentLetter")
    private LetterType letterType;

    @Schema(description = "검색할 내용", example = "test")
    private String searchData;
}
