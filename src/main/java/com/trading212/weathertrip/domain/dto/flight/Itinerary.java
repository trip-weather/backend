package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Itinerary {
    private String duration;
    private List<Segment> segments;
}
