package com.trading212.weathertrip.services.weather;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.weather.WeatherLocationDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
public class WeatherLocationService {
    private final RestTemplate restTemplate;

    public WeatherLocationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findLocation(String city) throws JsonProcessingException {
        String url = WEATHER_LOCATION_URL + city;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, WEATHER_API_KEY );
        headers.set(RAPID_API_HOST, WEATHER_API_HOST);

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        List<WeatherLocationDTO> locations = objectMapper.readValue(body, new TypeReference<List<WeatherLocationDTO>>() {
        });

        return locations.get(0).getPlace_id();
    }
}