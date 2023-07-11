package com.trading212.weathertrip.services.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.constants.Constants;
import com.trading212.weathertrip.domain.dto.weather.ForecastDataDTO;
import com.trading212.weathertrip.domain.dto.weather.WrapperForecastDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
public class ForecastService {
    private final RestTemplate restTemplate;
    private final WeatherLocationService weatherLocationService;


    public ForecastService(RestTemplate restTemplate, WeatherLocationService weatherLocationService) {
        this.restTemplate = restTemplate;
        this.weatherLocationService = weatherLocationService;
    }

    public ForecastDataDTO getDailyForecastForPlace(String city) throws JsonProcessingException {
        String place_id = weatherLocationService.findLocation(city);
        String url = Constants.FORECAST_URL + place_id;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, WEATHER_API_KEY );
        headers.set(RAPID_API_HOST, WEATHER_API_HOST);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        WrapperForecastDTO forecasts = objectMapper.readValue(body, new TypeReference<WrapperForecastDTO>() {
        });

        return forecasts.getDaily();
    }
}
