package com.trading212.weathertrip.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.domain.dto.hotel.HotelDetailsDTO;
import com.trading212.weathertrip.services.hotel.HotelService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class HotelController {
    private final HotelService hotelService;

    public HotelController(HotelService hotelService) {
        this.hotelService = hotelService;
    }

    @GetMapping("/hotel/{id}")
    public ResponseEntity<HotelDetailsDTO> getHotel(@PathVariable(name = "id") Integer externalId,
                                                    @RequestParam("checkIn") String checkInDate,
                                                    @RequestParam("checkOut") String checkOutDate) throws JsonProcessingException {

        return ResponseEntity.ok(hotelService.getHotel(externalId, checkInDate, checkOutDate));
    }
}
