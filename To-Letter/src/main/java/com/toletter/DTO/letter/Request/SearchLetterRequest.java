package com.toletter.DTO.letter.Request;

import com.toletter.Enums.LetterType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class SearchLetterRequest {
    @Schema(description = "검색할 letter 종류", example = "receivedLetter / sentLetter")
    private LetterType letterType;

    @Schema(description = "검색할 내용", example = "test")
    private String searchData;

    @Schema(description = "페이징 pageNumber", example = "pageNumber, pageSize, sort")
    private int pageNumber;

    @Schema(description = "페이징 pageSize", example = "pageNumber, pageSize, sort")
    private int pageSize;
}
