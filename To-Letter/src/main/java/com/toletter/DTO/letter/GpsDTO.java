package com.toletter.DTO.letter;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GpsDTO {
    @ApiModelProperty(value = "lat1", example = "37.1212")
    private double lat1;

    @ApiModelProperty(value = "lat2", example = "37.1213")
    private double lat2;

    @ApiModelProperty(value = "lon1", example = "126.1212")
    private double lon1;

    @ApiModelProperty(value = "lon2", example = "126.1213")
    private double lon2;

}
