package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.WeatherException;
import com.trading212.weathertrip.domain.constants.Constants;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import com.trading212.weathertrip.domain.dto.weather.ForecastDataDTO;
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

    public SearchService(HotelService hotelService, ForecastService forecastService) {
        this.hotelService = hotelService;
        this.forecastService = forecastService;
    }

    public List<WrapperHotelDTO> search(String city,
                                        String minTemp,
                                        String maxTemp,
                                        Integer period) throws IOException {
        Map<LocalDate, LocalDate> periods = new LinkedHashMap<>();

        //CITY
        if (city != null) {
            //CITY + TEMP OR CITY + TEMP + PERIOD
            if (minTemp != null && maxTemp != null) {
                List<ForecastDTO> weatherData = forecastService.getWeatherData(city);
                if (weatherData == null) {
                    forecastService.saveForecast(city);
                    weatherData = forecastService.getWeatherData(city);
                }
                periods = getAppropriatePeriodsForCity(weatherData, minTemp, maxTemp, period);
                //CITY + PERIOD OR CITY

            } else {
                periods.put(DEFAULT_START_DATE, period != null ? LocalDate.now().plusDays(period) : DEFAULT_END_DATE);
            }

            if (periods.isEmpty()) {
                throw new WeatherException("No options found for the minimum and maximum temperature you submitted.");
            }
            return hotelService.findAvailableHotels(periods, city);
        }

        // PERIOD
        if (period != null && minTemp == null && maxTemp == null) {
            periods.put(DEFAULT_START_DATE, LocalDate.now().plusDays(period));
            return List.of(new WrapperHotelDTO(hotelService.findAvailableHotelsForAllCities(periods)));
        }

        // PERIOD + TEMP OR ONLY TEMP
        List<List<ForecastDTO>> weatherDataForTargetCities = forecastService.getWeatherDataForTargetCities();
        periods = getAppropriatePeriodsForAllCities(weatherDataForTargetCities, minTemp, maxTemp, period);

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

    public List<String> getCities() {
        return Constants.GET_CITIES;
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
