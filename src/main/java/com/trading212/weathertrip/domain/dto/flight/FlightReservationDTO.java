package com.trading212.weathertrip.domain.dto.flight;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class FlightReservationDTO {

    private String reservationDate;

    private String price;

    private String departingAt;

    private String arrivingAt;

    private String from;

    private String to;

    private String currency;

    private String paymentStatus;
}
