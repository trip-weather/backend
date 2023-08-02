package com.trading212.weathertrip.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleMapsLocation {
    @JsonProperty("lat")
    private String latitude;

    @JsonProperty("lng")
    private String longitude;

}
