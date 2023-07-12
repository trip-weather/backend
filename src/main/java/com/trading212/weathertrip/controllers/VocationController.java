package com.trading212.weathertrip.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.domain.dto.VocationValidation;
import com.trading212.weathertrip.domain.dto.hotel.HotelResultDTO;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.services.VocationService;
import com.trading212.weathertrip.services.hotel.HotelService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class VocationController {
    private final VocationService vocationService;
    private final HotelService hotelService;


    public VocationController(VocationService vocationService, HotelService hotelService) {
        this.vocationService = vocationService;
        this.hotelService = hotelService;
    }

    @GetMapping("/find-vacation")
    public ResponseEntity<List<WrapperHotelDTO>> findVocation(@RequestBody @Valid VocationValidation validation) throws JsonProcessingException {
        return ResponseEntity.ok(vocationService.findVocations(validation));
    }

    @GetMapping("/suggested-hotels")
    public ResponseEntity<List<HotelResultDTO>> getHotels() throws JsonProcessingException {
        return ResponseEntity.ok(hotelService.getHotels());
    }

    @GetMapping("/get/cities")
    public ResponseEntity<List<String>> getCities() {
        return ResponseEntity.ok(vocationService.getCities());
    }
}
