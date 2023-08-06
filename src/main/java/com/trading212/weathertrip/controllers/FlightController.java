package com.trading212.weathertrip.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.controllers.validation.FlightValidation;
import com.trading212.weathertrip.domain.dto.flight.FlightResponseWrapper;
import com.trading212.weathertrip.services.FlightService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class FlightController {

    private final FlightService flightService;

    public FlightController(FlightService testFlightService) {
        this.flightService = testFlightService;
    }

    @GetMapping("/flights")
    public ResponseEntity<FlightResponseWrapper> searchFlights(FlightValidation validation) throws JsonProcessingException {
        return ResponseEntity.ok(flightService.searchFlights(validation));
    }
}
