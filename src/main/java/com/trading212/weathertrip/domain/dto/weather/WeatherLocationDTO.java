package com.trading212.weathertrip.domain.dto.weather;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
public class WeatherLocationDTO {
    @JsonProperty
    private String name;
    @JsonProperty
    private String place_id;
    @JsonProperty
    private String country;

    public WeatherLocationDTO(@JsonProperty String name, @JsonProperty String place_id, @JsonProperty String country) {
        this.name = name;
        this.place_id = place_id;
        this.country = country;
    }
    public WeatherLocationDTO() {
    }
}
