package com.trading212.weathertrip.domain.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class VocationValidation {
    private String city;
    private Integer minTemp;
    private Integer maxTemp;
    private Integer period;
}
