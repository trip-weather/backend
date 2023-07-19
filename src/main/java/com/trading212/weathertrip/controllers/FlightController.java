package com.trading212.weathertrip.controllers;

import com.amadeus.exceptions.ResponseException;
import com.amadeus.resources.Location;
import com.trading212.weathertrip.controllers.validation.FlightValidationDTO;
import com.trading212.weathertrip.domain.dto.flight.FlightOfferDTO;
import com.trading212.weathertrip.services.AmadeusConnect;
import com.trading212.weathertrip.services.FlightService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @GetMapping("/locations")
    public Location[] locations(@RequestParam(required = true) String keyword) throws ResponseException {
        return AmadeusConnect.INSTANCE.location(keyword);
    }

    @GetMapping("/flights")
    public FlightOfferDTO[] flights(@RequestBody @Valid FlightValidationDTO validation)
            throws ResponseException {
        return flightService.findTwoWayTickets(validation);
    }
}
