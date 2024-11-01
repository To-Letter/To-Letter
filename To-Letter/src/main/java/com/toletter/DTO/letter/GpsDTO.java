package com.toletter.DTO.letter;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GpsDTO {
    @Schema(description = "lat1", example = "37.1212")
    private double lat1;

    @Schema(description = "lat2", example = "37.1213")
    private double lat2;

    @Schema(description = "lon1", example = "126.1212")
    private double lon1;

    @Schema(description = "lon2", example = "126.1213")
    private double lon2;

}
