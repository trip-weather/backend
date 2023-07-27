package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
public class Reservation {
    private int id;
    private String userUuid;
    private String hotelUuid;
    private String orderUuid;
    private LocalDateTime reservationDate;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private BigDecimal price;
}
