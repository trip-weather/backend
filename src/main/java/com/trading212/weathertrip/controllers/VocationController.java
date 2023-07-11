package com.trading212.weathertrip.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.domain.dto.VocationValidation;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.services.VocationService;
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


    public VocationController(VocationService vocationService) {
        this.vocationService = vocationService;
    }

    @GetMapping("/find-vacation")
    public ResponseEntity<List<WrapperHotelDTO>> findVocation(@RequestBody @Valid VocationValidation validation) throws JsonProcessingException {
        return ResponseEntity.ok(vocationService.findVocations(validation));
    }
}
