package com.trading212.weathertrip.domain.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class UserReservedHotelsDTO {

    private String reservationDate;

    private String checkInDate;

    private String checkOutDate;

    private BigDecimal price;

    private String photoMainUrl;

    private String city;

    private Integer externalId;

    private String currency;
}
