package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Price {
    private String currency;
    private String total;
}
