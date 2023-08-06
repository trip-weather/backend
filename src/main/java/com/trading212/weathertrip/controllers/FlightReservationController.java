package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.dto.flight.FlightReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedFlightsDTO;
import com.trading212.weathertrip.services.FlightReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FlightReservationController {
    private final FlightReservationService flightReservationService;

    public FlightReservationController(FlightReservationService flightReservationService) {
        this.flightReservationService = flightReservationService;
    }

    @GetMapping("/user/profile/reservations/flight-tickets")
    public ResponseEntity<List<UserReservedFlightsDTO>> getUserReservedHotels(@RequestParam(name = "status") String status) {
        return ResponseEntity.ok(flightReservationService.getUserReservedFlights(status));
    }

    @GetMapping("/flight-order/{uuid}")
    public ResponseEntity<List<FlightReservationDTO>> getHotelReservation(@PathVariable String uuid) {
        List<FlightReservationDTO> reservation = flightReservationService.getReservationByOrderUuid(uuid);
        return ResponseEntity.ok().body(reservation);
    }
}
