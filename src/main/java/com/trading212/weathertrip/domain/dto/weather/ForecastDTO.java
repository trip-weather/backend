package com.trading212.weathertrip.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastDTO implements Serializable {
    @JsonProperty
    private String day;
    @JsonProperty
    private String weather;
    @JsonProperty
    private String summary;
    @JsonProperty
    private double temperature_min;
    @JsonProperty
    private double temperature_max;
    @JsonProperty
    private double feels_like_max;

    public ForecastDTO(@JsonProperty String day, @JsonProperty String weather,
                       @JsonProperty String summary, @JsonProperty double temperature_min,
                       @JsonProperty double temperature_max, @JsonProperty double feels_like_max) {
        this.day = day;
        this.weather = weather;
        this.summary = summary;
        this.temperature_min = temperature_min;
        this.temperature_max = temperature_max;
        this.feels_like_max = feels_like_max;
    }
    public ForecastDTO() {
    }
}
