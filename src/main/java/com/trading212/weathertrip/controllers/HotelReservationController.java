package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.dto.HotelReservationDTO;
import com.trading212.weathertrip.services.HotelReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HotelReservationController {

    private final HotelReservationService hotelReservationService;

    public HotelReservationController(HotelReservationService reservationService) {
        this.hotelReservationService = reservationService;
    }

    @GetMapping("/order/{uuid}")
    public ResponseEntity<HotelReservationDTO> getReservation(@PathVariable String uuid) {
        HotelReservationDTO reservation = hotelReservationService.getReservationByOrderUuid(uuid);

        return ResponseEntity.ok().body(reservation);
    }
}
