package com.trading212.weathertrip.domain.dto.hotel;

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
public class HotelResultDTO {
    @JsonProperty
    private int id;
    @JsonProperty
    private String name;
    @JsonProperty
    private int mainPhotoId;
    @JsonProperty
    private String photoMainUrl;
    @JsonProperty
    private List<String> photoUrls;
    @JsonProperty
    private String countryCode;
    @JsonProperty
    private double latitude;
    @JsonProperty
    private double longitude;
    @JsonProperty
    private HotelPrice priceBreakdown;
    @JsonProperty
    private String currency;
    @JsonProperty
    private CheckinTime checkin;
    @JsonProperty
    private CheckoutTime checkout;
    @JsonProperty
    private String checkoutDate;
    @JsonProperty
    private String checkinDate;
    @JsonProperty
    private double reviewScore;
    @JsonProperty
    private String reviewScoreWord;
    @JsonProperty
    private int reviewCount;
    @JsonProperty
    private int qualityClass;
    @JsonProperty
    private int propertyClass; // ***
    @JsonProperty
    private String wishlistName;
}
