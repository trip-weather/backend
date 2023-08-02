package com.trading212.weathertrip.services.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import com.trading212.weathertrip.domain.dto.weather.WrapperForecastDTO;
import com.trading212.weathertrip.services.RedisService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;
import static com.trading212.weathertrip.domain.constants.Constants.GET_CITIES;

@Service
public class ForecastService {
    private static final String HASH_KEY = "city_forecast";
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final WeatherLocationService weatherLocationService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, List<ForecastDTO>> hashOperations;
    private final RedisService redisService;


    public ForecastService(RestTemplate restTemplate,
                           ObjectMapper objectMapper,
                           WeatherLocationService weatherLocationService,
                           RedisTemplate<String, Object> redisTemplate,
                           @Qualifier("weatherHashOperations") HashOperations<String, String, List<ForecastDTO>> hashOperations, RedisService redisService) {
        this.objectMapper = objectMapper;
        this.restTemplate = restTemplate;
        this.weatherLocationService = weatherLocationService;
        this.redisTemplate = redisTemplate;
        this.hashOperations = hashOperations;
        this.redisService = redisService;
    }

    public List<List<ForecastDTO>> getWeatherDataForTargetCities() throws JsonProcessingException {
        List<List<ForecastDTO>> result = new ArrayList<>();
        for (String city : GET_CITIES) {
            List<ForecastDTO> weatherData = redisService.getWeatherData(city);

            if (weatherData == null) {
                weatherData = getForecast(city);
                redisService.saveForecast(city, weatherData);
            }
            result.add(weatherData);
        }
        return result;
    }

    public List<String> getAllCities() {
        Set<Object> keys = redisTemplate.opsForHash().keys(HASH_KEY);
        return keys.stream()
                .filter(key -> key instanceof String)
                .map(key -> (String) key)
                .collect(Collectors.toList());
    }

    public void updateForecast() throws JsonProcessingException {
        List<String> cities = getAllCities();
        for (String city : cities) {
            hashOperations.put(HASH_KEY, city, getForecast(city));
        }
    }

    private List<List<ForecastDTO>> getForecastForAllCities() throws JsonProcessingException {
        List<List<ForecastDTO>> result = new ArrayList<>();

        for (String city : getAllCities()) {
            result.add(getForecast(city));
        }

        return result;
    }

    public List<ForecastDTO> getForecast(String city) throws JsonProcessingException {
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
            List<ForecastDTO> forecast = getForecast(city);
            redisService.saveForecast(city, getForecast(city));
            result.add(forecast);
        }
        return result;
    }
}
