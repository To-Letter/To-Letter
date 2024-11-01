package com.toletter.DTO;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResponseDTO {
    @Schema(description = "상태 코드", example = "200")
    private int responseCode;

    @Schema(description = "메시지", example = "정상처리되었습니다.")
    private String responseMessage;

    @Schema(description = "데이터")
    private Object responseData;

    public static ResponseDTO res(int responseCode, String responseMessage, Object responseData) {
        return ResponseDTO.builder()
                .responseCode(responseCode)
                .responseMessage(responseMessage)
                .responseData(responseData)
                .build();
    }
}
