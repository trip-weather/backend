package com.trading212.weathertrip.domain.dto.hotel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class HotelReservationDates {
    private String checkInDate;
    private String checkOutDate;
}
