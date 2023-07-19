package com.trading212.weathertrip.domain.dto.hotel;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FavouriteHotelDTO {
    private String uuid;
    private String name;
    private String photoMainUrl;
    private int favouriteCount;
    private String city;
    private int externalId;
}
