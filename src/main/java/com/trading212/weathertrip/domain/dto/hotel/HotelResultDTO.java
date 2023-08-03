package com.trading212.weathertrip.domain.dto.hotel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class HotelResultDTO {
    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

    @JsonProperty
    private String photoMainUrl;
        
    @JsonProperty
    private HotelPrice priceBreakdown;

    @JsonProperty
    private String latitude;

    @JsonProperty
    private String longitude;

    @JsonProperty
    private String currency;

    @JsonProperty
    private String checkoutDate;

    @JsonProperty
    private String checkinDate;

    @JsonProperty
    private double reviewScore;

    @JsonProperty
    private String reviewScoreWord;

    @JsonProperty
    private int qualityClass;

    @JsonProperty
    private int propertyClass; // ***

    @JsonProperty
    private String wishlistName;

    private Map<String, Integer> nearbyFilters;
}
