package com.toletter.DTO.letter.Request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@RequiredArgsConstructor
public class DeleteLetterRequest {
    @Schema(description = "삭제할 letter_id 리스트", example = "[1,2,3]")
    private List<Long> letterIds;

}
