package com.trading212.weathertrip.domain.dto.flight;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FlightSegment {
    @JsonProperty("departing_at")
    private String departingAt;
    @JsonProperty("arriving_at")
    private String arrivingAt;
}
