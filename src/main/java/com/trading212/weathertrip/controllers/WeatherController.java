package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.services.hotel.HotelServiceLocation;
import com.trading212.weathertrip.services.weather.ForecastService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class WeatherController {
    private final ForecastService forecastService;
    private final HotelServiceLocation hotelServiceLocation;

    public WeatherController(ForecastService forecastService, HotelServiceLocation hotelServiceLocation) {
        this.forecastService = forecastService;
        this.hotelServiceLocation = hotelServiceLocation;
    }

    @GetMapping("/location")
    public HttpStatus getLocation() throws IOException {
//         Testing
//        forecastService.getDailyForecastForPlace("Sofia");
//        hotelServiceLocation.findDestinationId("Plovdiv");
        return HttpStatus.OK;

    }
}
