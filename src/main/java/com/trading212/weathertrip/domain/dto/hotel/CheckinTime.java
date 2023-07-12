package com.trading212.weathertrip.domain.dto.hotel;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class CheckinTime {
    private String untilTime;
    private String fromTime;
}