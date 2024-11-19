package com.toletter.DTO.letter.Response;

import com.toletter.DTO.letter.LetterDTO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Data
@RequiredArgsConstructor
public class LetterResponse {
    @Schema(description = "현재 페이지", example = "1")
    private Pageable pageable;

    @Schema(description = "현재 페이지", example = "1")
    private List<LetterDTO> letterDTO;

    public static LetterResponse res(List<LetterDTO> letterDTO, Pageable pageable) {
        LetterResponse res = new LetterResponse();
        res.pageable = pageable;
        res.letterDTO = letterDTO;
        return res;
    }


}
