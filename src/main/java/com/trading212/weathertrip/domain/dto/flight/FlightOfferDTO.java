package com.trading212.weathertrip.domain.dto.flight;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class FlightOfferDTO {
    private String type;
    private String id;
    private String source;
    private boolean instantTicketingRequired;
    private boolean oneWay;
    private String lastTicketingDate;
    private int numberOfBookableSeats;
    private List<Itinerary> itineraries;
    private Price price;
    private List<TravelerPricing> travelerPricings;
}
