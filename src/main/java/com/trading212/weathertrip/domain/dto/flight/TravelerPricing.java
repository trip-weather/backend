package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class TravelerPricing {
    private String travelerId;
    private String fareOption;
    private String travelerType;
    private Price price;
    private List<FareDetailsBySegment> fareDetailsBySegment;
}
