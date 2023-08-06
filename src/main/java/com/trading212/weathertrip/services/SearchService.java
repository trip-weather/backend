package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.WeatherException;
import com.trading212.weathertrip.controllers.validation.SearchHotelValidation;
import com.trading212.weathertrip.domain.constants.Constants;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import com.trading212.weathertrip.services.hotel.HotelService;
import com.trading212.weathertrip.services.weather.ForecastService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
public class SearchService {
    private final HotelService hotelService;
    private final ForecastService forecastService;
    private final RedisService redisService;

    public SearchService(HotelService hotelService, ForecastService forecastService, RedisService redisService) {
        this.hotelService = hotelService;
        this.forecastService = forecastService;
        this.redisService = redisService;
    }

    public List<WrapperHotelDTO> search(SearchHotelValidation validation) throws IOException {

        String city = validation.getCity();
        String minTemp = validation.getMinTemp();
        String maxTemp = validation.getMaxTemp();
        Integer period = validation.getPeriod();
        List<String> filters = validation.getFilters();

        Map<LocalDate, LocalDate> periods = new LinkedHashMap<>();

        //CITY
        if (city != null) {
            //CITY + TEMP OR CITY + TEMP + PERIOD
            if (minTemp != null && maxTemp != null) {
                List<ForecastDTO> forecastData = redisService.getForecastForCity(city);
                if (forecastData == null) {
                    List<ForecastDTO> forecast = forecastService.getForecastforCityFromApi(city);
                    redisService.saveForecast(city, forecast);
//                    forecastData = redisService.getForecastForCity(city);
                    forecastData = forecast;
                }
                periods = getAppropriatePeriodsForCity(forecastData, minTemp, maxTemp, period);

                //CITY + PERIOD OR CITY
            } else {
                periods.put(DEFAULT_START_DATE, period != null ? LocalDate.now().plusDays(period) : DEFAULT_END_DATE);
            }

            if (periods.isEmpty()) {
                throw new WeatherException("No options found for the minimum and maximum temperature you submitted.");
            }
            return hotelService.findAvailableHotels(periods, city, filters);
        }

        // PERIOD
        if (period != null && minTemp == null && maxTemp == null) {
            periods.put(DEFAULT_START_DATE, LocalDate.now().plusDays(period));
            return List.of(new WrapperHotelDTO(hotelService.findAvailableHotelsForAllCities(periods)));
        }

        // PERIOD + TEMP OR ONLY TEMP
        List<List<ForecastDTO>> forecastDataForTargetCities = forecastService.getForecastForTargetCities();
        if (forecastDataForTargetCities.isEmpty()) {
            forecastDataForTargetCities = forecastService.saveForecastForTargetCities();
        }
        periods = getAppropriatePeriodsForAllCities(forecastDataForTargetCities, minTemp, maxTemp, period);

        if (periods.isEmpty()) {
            throw new WeatherException("No options found for the minimum and maximum temperature you submitted.");
        }

        return List.of(new WrapperHotelDTO(hotelService.findAvailableHotelsForAllCities(periods)));
    }


    private HashMap<LocalDate, LocalDate> getAppropriatePeriodsForAllCities(List<List<ForecastDTO>> forecasts,
                                                                            String minTemp,
                                                                            String maxTemp,
                                                                            Integer period) {

        LinkedHashMap<LocalDate, LocalDate> result = new LinkedHashMap<>();

        if (period == null) {
            period = Constants.DEFAULT_PERIOD;
        }

        for (List<ForecastDTO> forecast : forecasts) {
            getPeriod(minTemp, maxTemp, result, period, forecast);
        }
        return result;
    }

    private Map<LocalDate, LocalDate> getAppropriatePeriodsForCity(List<ForecastDTO> forecast,
                                                                   String minTemp,
                                                                   String maxTemp, Integer period) {
        LinkedHashMap<LocalDate, LocalDate> result = new LinkedHashMap<>();

        if (period == null) {
            period = Constants.DEFAULT_PERIOD;
        }

        getPeriod(minTemp, maxTemp, result, period, forecast);
        return result;
    }

    private void getPeriod(String minTemp,
                           String maxTemp,
                           LinkedHashMap<LocalDate, LocalDate> result,
                           Integer period,
                           List<ForecastDTO> forecast) {

        double minTempValue = Double.parseDouble(minTemp);
        double maxTempValue = Double.parseDouble(maxTemp);

        for (int i = 0; i <= forecast.size() - period; i++) {
            boolean validPeriod = true;

            for (int j = i; j < i + period; j++) {
                ForecastDTO data = forecast.get(j);
                double forecastMaxTemp = data.getTemperature_max();

                if (forecastMaxTemp < minTempValue || forecastMaxTemp > maxTempValue) {
                    validPeriod = false;
                    break;
                }
            }
            if (validPeriod) {
                String startDateString = forecast.get(i).getDay();
                String endDateString = forecast.get(i + period - 1).getDay();

                LocalDate startDate = LocalDate.parse(startDateString, DATE_FORMATTER);
                LocalDate endDate = LocalDate.parse(endDateString, DATE_FORMATTER);

                result.put(startDate, endDate);
            }
        }
    }
}
