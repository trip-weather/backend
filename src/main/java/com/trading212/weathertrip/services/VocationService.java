package com.trading212.weathertrip.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.trading212.weathertrip.controllers.errors.WeatherException;
import com.trading212.weathertrip.domain.constants.Constants;
import com.trading212.weathertrip.domain.dto.VocationValidation;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.domain.dto.weather.ForecastDataDTO;
import com.trading212.weathertrip.services.hotel.HotelService;
import com.trading212.weathertrip.services.weather.ForecastService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
public class VocationService {
    private final HotelService hotelService;
    private final ForecastService forecastService;


    public VocationService(HotelService hotelService, ForecastService forecastService) {
        this.hotelService = hotelService;
        this.forecastService = forecastService;
    }

    public List<WrapperHotelDTO> findVocations(VocationValidation validation) throws JsonProcessingException {
        // CITY
        if (validation.getCity() != null) {
            ForecastDataDTO forecast = forecastService.getForecastForPlace(validation.getCity());
            // CITY + PERIOD + TEMP  or CITY + TEMP with default period
            if (validation.getMaxTemp() != null && validation.getMaxTemp() != null) {
                HashMap<LocalDate, LocalDate> periods = getSuitablePeriodsForCity(forecast, validation);

                if (periods.isEmpty()) {
                    throw new WeatherException("No options found for the minimum and maximum temperature you submitted");
                }
                return hotelService.findAvailableHotels(periods, validation.getCity());
            }
            // CITY + PERIOD or only CITY
            else {
                LinkedHashMap<LocalDate, LocalDate> periods = new LinkedHashMap<>();
                if (validation.getPeriod() == null) {
                    periods.put(DEFAULT_START_DATE, DEFAULT_END_DATE);
                } else {
                    periods.put(DEFAULT_START_DATE, LocalDate.now().plusDays(validation.getPeriod()));
                }
                return hotelService.findAvailableHotels(periods, validation.getCity());
            }
        }
        // PERIOD
        else if (validation.getPeriod() != null) {
            LinkedHashMap<LocalDate, LocalDate> periods = new LinkedHashMap<>();
            periods.put(DEFAULT_START_DATE, LocalDate.now().plusDays(validation.getPeriod()));

            return List.of(new WrapperHotelDTO(hotelService.findAvailableHotelsForAllCities(periods)));
        }

        // TEMPERATURE + TEMPERATURE + PERIOD  -> should return 5 hotels for every city corresponding to periods
        List<ForecastDataDTO> forecastForAllCities = forecastService.getForecastForAllCities();
        HashMap<LocalDate, LocalDate> periods = getSuitablePeriodsForAllCities(validation, forecastForAllCities);

        if (periods.isEmpty()) {
            throw new WeatherException("No options found for the minimum and maximum temperature you submitted");
        }

        return List.of(new WrapperHotelDTO(hotelService.findAvailableHotelsForAllCities(periods)));
    }

    private HashMap<LocalDate, LocalDate> getSuitablePeriodsForAllCities(VocationValidation validation,
                                                                         List<ForecastDataDTO> forecasts) throws JsonProcessingException {

        LinkedHashMap<LocalDate, LocalDate> result = new LinkedHashMap<>();

        Integer period = validation.getPeriod();
        if (period == null) {
            period = Constants.DEFAULT_PERIOD;
        }

        for (ForecastDataDTO forecast : forecasts) {
            getPeriod(validation, result, period, forecast);
        }
        return result;
    }

    private HashMap<LocalDate, LocalDate> getSuitablePeriodsForCity(ForecastDataDTO forecast, VocationValidation validation) {
        LinkedHashMap<LocalDate, LocalDate> result = new LinkedHashMap<>();

        Integer period = validation.getPeriod();
        if (period == null) {
            period = Constants.DEFAULT_PERIOD;
        }

        getPeriod(validation, result, period, forecast);
        return result;
    }

    public List<String> getCities() {
        return Constants.GET_CITIES;
    }

    private void getPeriod(VocationValidation validation, LinkedHashMap<LocalDate, LocalDate> result, Integer period, ForecastDataDTO forecast) {
        for (int i = 0; i < forecast.getData().length - period + 1; i++) {
            boolean validPeriod = true;
            for (int j = i; j < i + period; j++) {
                double minTemp = forecast.getData()[j].getTemperature_min();
                double maxTemp = forecast.getData()[j].getTemperature_max();

                if (minTemp < validation.getMinTemp() || maxTemp > validation.getMaxTemp()) {
                    validPeriod = false;
                    break;
                }
            }
            if (validPeriod) {
                String startDateString = forecast.getData()[i].getDay();
                String endDateString = forecast.getData()[i + period - 1].getDay();

                LocalDate startDate = LocalDate.parse(startDateString, DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(endDateString, DATE_FORMATTER);

                result.put(startDate, endDate);
            }
        }
    }
}
