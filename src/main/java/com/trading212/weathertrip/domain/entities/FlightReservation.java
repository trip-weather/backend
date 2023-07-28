package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Data
@Builder
public class FlightReservation {

    private Integer id;

    private String userUuid;

    private String orderUuid;

    private String flightUuid;

    private LocalDate reservationDate;

    private BigDecimal price;
}
