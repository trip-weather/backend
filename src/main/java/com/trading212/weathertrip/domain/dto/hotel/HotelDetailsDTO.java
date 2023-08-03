package com.trading212.weathertrip.domain.dto.hotel;

import com.trading212.weathertrip.domain.dto.GooglePlacesResultDTO;
import com.trading212.weathertrip.domain.dto.hotelDetailsData.Properties;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@RequiredArgsConstructor
public class HotelDetailsDTO {
    private int hotelId;

    private String name;

    private List<HotelPhoto> photos;

    private List<Properties> properties;

    private String description;

    private Integer favouriteCount;

    private double pricePerDay;

    private Double totalPrice;

    private String arrivalDate;

    private String departureDate;

    private double reviewScore;

    private String reviewScoreWord;

    private double rating;

    private String country;

    private String address;

    private String city;

    private String latitude;

    private String longitude;

    private String url;

    private int nights;

    private Map<String, List<GooglePlacesResultDTO>> nearby;

}
