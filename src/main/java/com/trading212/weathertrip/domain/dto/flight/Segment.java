package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Segment {
    private Departure departure;
    private Arrival arrival;
    private String number;
    private String duration;
    private String id;
    private int numberOfStops;
}
