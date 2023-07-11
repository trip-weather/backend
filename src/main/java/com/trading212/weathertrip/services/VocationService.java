package com.trading212.weathertrip.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.domain.dto.VocationValidation;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.domain.dto.weather.ForecastDataDTO;
import com.trading212.weathertrip.services.hotel.HotelService;
import com.trading212.weathertrip.services.weather.ForecastService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

@Service
public class VocationService {
    private final HotelService hotelService;
    private final ForecastService forecastService;


    public VocationService(HotelService hotelService, ForecastService forecastService) {
        this.hotelService = hotelService;
        this.forecastService = forecastService;
    }

    public List<WrapperHotelDTO> findVocations(VocationValidation validation) throws JsonProcessingException {

        ForecastDataDTO forecast = forecastService.getDailyForecastForPlace(validation.getCity());

        HashMap<LocalDate, LocalDate> periods = new LinkedHashMap<>();

        for (int i = 0; i < forecast.getData().length - validation.getPeriod() + 1; i++) {
            boolean validPeriod = true;
            for (int j = i; j < i + validation.getPeriod(); j++) {
                double minTemp = forecast.getData()[j].getTemperature_min();
                double maxTemp = forecast.getData()[j].getTemperature_max();

                if (minTemp < validation.getMinTemp() || maxTemp > validation.getMaxTemp()) {
                    validPeriod = false;
                    break;
                }
            }
            if (validPeriod) {
                String startDateString = forecast.getData()[i].getDay();
                String endDateString = forecast.getData()[i + validation.getPeriod() - 1].getDay();

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate startDate = LocalDate.parse(startDateString, formatter);
                LocalDate endDate = LocalDate.parse(endDateString, formatter);

                periods.put(startDate, endDate);
            }
        }
        return hotelService.findAvailableHotels(periods, validation.getCity());
    }
}
