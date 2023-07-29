package com.trading212.weathertrip.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HotelReservationDTO {
    private String checkInDate;
    private String checkOutDate;
    private BigDecimal price;
    private String hotelName;
    private String city;
    private String photoMainUrl;
    private String paymentStatus;
}
