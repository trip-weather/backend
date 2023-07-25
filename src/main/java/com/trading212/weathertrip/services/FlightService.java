package com.trading212.weathertrip.services;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.amadeus.resources.Location;
import com.trading212.weathertrip.controllers.errors.FlightException;
import com.trading212.weathertrip.controllers.validation.FlightValidationDTO;
import com.trading212.weathertrip.domain.dto.flight.FlightOfferDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FlightService {
    private final ModelMapper modelMapper;
    private Map<String, Location.Address> airportCache = new HashMap<>();


    public FlightService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }


    public FlightOfferDTO[] findTwoWayTickets(FlightValidationDTO validation) throws ResponseException {

        String origin = getIataCode(validation.getOrigin());
        String destination = getIataCode(validation.getDestination());

        FlightOfferSearch[] flightOffers = AmadeusConnect.INSTANCE.getFlightOffer(origin,
                destination, validation.getDepartDate(), validation.getAdults(), validation.getReturnDate());

        if (flightOffers == null || flightOffers.length == 0) {
            throw new FlightException("Could not find flights for both departure and arrival cities.");
        }

        FlightOfferDTO[] map = modelMapper.map(flightOffers, FlightOfferDTO[].class);

//        for (FlightOfferDTO flightOfferDTO : map) {
//            for (Itinerary itinerary : flightOfferDTO.getItineraries()) {
//                for (Segment segment : itinerary.getSegments()) {
//                    String iataCode = segment.getArrival().getIataCode();
//
//                    Location.Address address;
//                    if (airportCache.containsKey(iataCode)) {
//                        address = airportCache.get(iataCode);
//                    } else {
//                        address = getAirportAddress(iataCode);
//                    }
//                    segment.getArrival().setCity(address.getCityName());
//                    segment.getArrival().setCountry(address.getCountryName());
//
//                }
//            }
//        }

        return map;
    }

    private String getIataCode(String city) throws FlightException, ResponseException {
        if ("Sofia".equals(city)) {
            return "SOF";
        } else {
            Location airport = AmadeusConnect.INSTANCE.findAirport(city);
            if (airport == null) {
                throw new FlightException("Could not find the nearest airport for the city: " + city);
            }
            return airport.getIataCode();
        }
    }

    private Location.Address getAirportAddress(String iataCode) throws FlightException {

        try {
            Location airport = AmadeusConnect.INSTANCE.findAirport(iataCode);
            if (airport == null) {
                throw new FlightException("Could not find the nearest airport for the city: " + iataCode);
            }
            return airport.getAddress();
        } catch (ResponseException e) {
            throw new FlightException("An error occurred while fetching airport details: " + e.getMessage());
        }
    }
}