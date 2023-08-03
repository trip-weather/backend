package com.trading212.weathertrip.domain.dto;

import com.trading212.weathertrip.domain.dto.hotel.HotelPhoto;
import com.trading212.weathertrip.domain.dto.hotelDetailsData.HotelData;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfo {
    private HotelData data;

    private List<HotelPhoto> photos;

    private String description;

}
