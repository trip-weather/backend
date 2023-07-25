package com.trading212.weathertrip.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.services.weather.ForecastService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Scheduler {

    private final ForecastService forecastService;

    public Scheduler(ForecastService forecastService) {
        this.forecastService = forecastService;
    }

    @Scheduled(cron = "0 0 12 * * *") // every day at 00:00
    public void updateForecast() throws JsonProcessingException {
        forecastService.updateForecast();
        log.info("Forecast updated!");
    }
}
