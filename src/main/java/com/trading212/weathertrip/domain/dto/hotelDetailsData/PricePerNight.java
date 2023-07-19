package com.trading212.weathertrip.domain.dto.hotelDetailsData;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PricePerNight {
    @JsonProperty
    private double value;
    @JsonProperty
    private String currency;
}
