package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FlightOfferDTO {
    private String type;
    private String id;
    private boolean oneWay;
    private String lastTicketingDate;
    private List<Itinerary> itineraries;
    private Price price;
    private List<TravelerPricing> travelerPricings;
}
