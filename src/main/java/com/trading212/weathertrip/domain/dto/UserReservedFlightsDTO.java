package com.trading212.weathertrip.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserReservedFlightsDTO {
    private String reservationDate;

    private String price;

    private String from;

    private String to;

    private String departingAt;

    private String arrivingAt;
}
