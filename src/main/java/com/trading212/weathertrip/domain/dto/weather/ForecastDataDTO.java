package com.trading212.weathertrip.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
public class ForecastDataDTO implements Serializable {
    @JsonProperty(namespace = "daily")
    List<ForecastDTO> data;

    public ForecastDataDTO(List<ForecastDTO> data) {
        this.data = data;
    }

    public ForecastDataDTO() {
    }
}