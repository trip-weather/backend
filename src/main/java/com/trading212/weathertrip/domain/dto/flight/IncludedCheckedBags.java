package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IncludedCheckedBags {
    private int quantity;
    private int weight;
    private String weightUnit;
}
