package com.trading212.weathertrip.domain.dto.hotelDetailsData;

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
public class HotelData {

    @JsonProperty("hotel_name")
    private String name;
    @JsonProperty("hotel_id")
    private int hotelId;

    @JsonProperty("longitude")
    private String longitude;

    @JsonProperty("latitude")
    private String latitude;

    @JsonProperty("url")
    private String url;

    @JsonProperty("composite_price_breakdown")
    private Price price;

    @JsonProperty("country_trans")
    private String country;

    @JsonProperty("city_trans")
    private String city;

    @JsonProperty("address")
    private String address;

    @JsonProperty("property_highlight_strip")
    private List<Properties> properties;

    @JsonProperty("rating")
    private double rating;
    @JsonProperty("review_score")
    private double reviewScore;

    @JsonProperty("review_score_word")
    private String reviewScoreWord;

    @JsonProperty("arrival_date")
    private String arrivalDate;

    @JsonProperty("departure_date")
    private String departureDate;
}
