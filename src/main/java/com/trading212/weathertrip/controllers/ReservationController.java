package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.dto.ReservationDTO;
import com.trading212.weathertrip.services.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/order/{uuid}")
    public ResponseEntity<ReservationDTO> getReservation(@PathVariable String uuid) {
        ReservationDTO reservation = reservationService.getReservationByOrderUuid(uuid);

        return ResponseEntity.ok().body(reservation);
    }
}
