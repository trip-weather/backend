package com.trading212.weathertrip.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class WrapperForecastDTO implements Serializable {
    @JsonProperty(namespace = "daily")
    ForecastDataDTO daily;

    public WrapperForecastDTO(ForecastDataDTO daily) {
        this.daily = daily;
    }

    public WrapperForecastDTO() {
    }
}
