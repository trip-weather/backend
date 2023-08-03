package com.trading212.weathertrip.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class GooglePlacesResultDTO {

    private String name;

    @JsonProperty("place_id")
    private String placeId;

    private String rating;

    private List<String> types;

    private String vicinity;

    @JsonProperty("geometry")
    private GoogleMapsGeometryDTO geometry;
}
