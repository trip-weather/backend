package com.trading212.weathertrip.domain.dto.hotel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class HotelDetailsDTO {
    private int hotelId;
    private String name;
    private List<HotelPhoto> photos;
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
}
