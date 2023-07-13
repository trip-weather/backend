package com.trading212.weathertrip.services;

import com.amadeus.Amadeus;
import com.amadeus.Params;
import com.amadeus.exceptions.ResponseException;
import com.amadeus.referencedata.Locations;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;

import static com.trading212.weathertrip.domain.constants.APIConstants.AMADEUS_API_KEY;
import static com.trading212.weathertrip.domain.constants.APIConstants.AMADEUS_API_SECRET;

public enum AmadeusConnect {
    INSTANCE;
    private final Amadeus amadeus;

    private AmadeusConnect() {
        this.amadeus = Amadeus
                .builder(AMADEUS_API_KEY, AMADEUS_API_SECRET)
                .build();
    }

    public Location[] location(String keyword) throws ResponseException {
        return amadeus.referenceData.locations.get(Params
                .with("keyword", keyword)
                .and("subType", Locations.AIRPORT));
    }

    public FlightOfferSearch[] flights(String origin, String destination, String departDate, String adults, String returnDate) throws ResponseException {
        return amadeus.shopping.flightOffersSearch.get(
                Params.with("originLocationCode", origin)
                        .and("destinationLocationCode", destination)
                        .and("departureDate", departDate)
                        .and("returnDate", returnDate)
                        .and("adults", adults)
                        .and("max", 5));
    }
}
