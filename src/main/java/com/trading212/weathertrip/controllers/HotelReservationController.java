package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.dto.hotel.HotelReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedHotelsDTO;
import com.trading212.weathertrip.services.hotel.HotelReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class HotelReservationController {

    private final HotelReservationService hotelReservationService;

    public HotelReservationController(HotelReservationService reservationService) {
        this.hotelReservationService = reservationService;
    }

    @GetMapping("/hotel-order/{uuid}")
    public ResponseEntity<HotelReservationDTO> getHotelReservation(@PathVariable String uuid) {
        HotelReservationDTO reservation = hotelReservationService.getReservationByOrderUuid(uuid);
        return ResponseEntity.ok().body(reservation);
    }

    @GetMapping("/reservations/hotel")
    public ResponseEntity<List<UserReservedHotelsDTO>> getUserReservedHotels(@RequestParam(name = "status") String status) {
        return ResponseEntity.ok(hotelReservationService.getUserReservedHotels(status));
    }
}
