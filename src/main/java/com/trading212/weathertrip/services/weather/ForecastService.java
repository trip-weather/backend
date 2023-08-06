package com.trading212.weathertrip.services.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import com.trading212.weathertrip.domain.dto.weather.WrapperForecastDTO;
import com.trading212.weathertrip.services.RedisService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;
import static com.trading212.weathertrip.domain.constants.Constants.GET_CITIES;

@Service
public class ForecastService {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherLocationService weatherLocationService;
    private final RedisService redisService;


    public ForecastService(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           WeatherLocationService weatherLocationService,
                           RedisService redisService) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.weatherLocationService = weatherLocationService;
        this.redisService = redisService;
    }

    public List<List<ForecastDTO>> getForecastForTargetCities() throws JsonProcessingException {
        List<List<ForecastDTO>> result = new ArrayList<>();
        for (String city : GET_CITIES) {
            List<ForecastDTO> weatherData = redisService.getForecastForCity(city);

            if (weatherData == null) {
                weatherData = getForecastforCityFromApi(city);
                redisService.saveForecast(city, weatherData);
            }
            result.add(weatherData);
        }
        return result;
    }

    public void updateForecast() throws JsonProcessingException {
        List<String> cities = redisService.getAllCitiesFromForecast();
        for (String city : cities) {
            redisService.updateForecast(city, getForecastforCityFromApi(city));
        }
    }

    public List<ForecastDTO> getForecastforCityFromApi(String city) throws JsonProcessingException {
        String place_id = weatherLocationService.findLocation(city);
        String url = FORECAST_URL + place_id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, WEATHER_API_KEY);
        headers.set(RAPID_API_HOST, WEATHER_API_HOST);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = response.getBody();
        WrapperForecastDTO forecasts = objectMapper.readValue(body, new TypeReference<>() {
        });
        return forecasts.getDaily().getData();
    }

    public List<List<ForecastDTO>> saveForecastForTargetCities() throws JsonProcessingException {
        List<List<ForecastDTO>> result = new ArrayList<>();
        for (String city : GET_CITIES) {
            List<ForecastDTO> forecast = getForecastforCityFromApi(city);
            redisService.saveForecast(city, getForecastforCityFromApi(city));
            result.add(forecast);
        }
        return result;
    }
}
