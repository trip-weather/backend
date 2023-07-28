package com.trading212.weathertrip.domain.dto.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightData {
    @JsonProperty("total_currency")
    private String currency;
    @JsonProperty("total_amount")
    private String totalAmount;
    @JsonProperty("slices")
    private List<FlightSlice> slices;
}
