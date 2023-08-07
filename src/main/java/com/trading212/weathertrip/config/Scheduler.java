package com.trading212.weathertrip.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.services.hotel.HotelService;
import com.trading212.weathertrip.services.weather.ForecastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scheduler {

    private final ForecastService forecastService;
    private final HotelService hotelService;

    public Scheduler(ForecastService forecastService, HotelService hotelService) {
        this.forecastService = forecastService;
        this.hotelService = hotelService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateForecast() throws JsonProcessingException {
        forecastService.updateForecast();
        log.info("Forecast updated!");
    }
    @Scheduled(cron = "0 0 0 * * TUE")
    public void updateDescriptionAndPhotos() throws JsonProcessingException {
      hotelService.updateDescriptionAndPhotos();
        log.info("Hotel description and photos updated!!");
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void updateHotelData() throws JsonProcessingException {
        hotelService.updateHotelData();
        log.info("Hotel data updated!!");
    }
}
