package com.trading212.weathertrip.services;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.FlightOfferSearch;
import com.trading212.weathertrip.controllers.validation.FlightValidationDTO;
import com.trading212.weathertrip.domain.dto.flight.FlightOfferDTO;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class FlightService {
    private final ModelMapper modelMapper;

    public FlightService(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public FlightOfferDTO[] findTwoWayTickets(FlightValidationDTO validation) throws ResponseException {
        FlightOfferSearch[] amadeusFlightOffer = AmadeusConnect.INSTANCE.flights(
                validation.getOrigin(),
                validation.getDestination(),
                validation.getDepartDate(),
                validation.getAdults(),
                validation.getReturnDate());

        return modelMapper.map(amadeusFlightOffer, FlightOfferDTO[].class);

    }
}
