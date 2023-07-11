package com.trading212.weathertrip.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class ForecastDataDTO implements Serializable {
    @JsonProperty(namespace = "daily")
    ForecastDTO[] data;

    public ForecastDataDTO(ForecastDTO[] data) {
        this.data = data;
    }

    public ForecastDataDTO() {
    }
}