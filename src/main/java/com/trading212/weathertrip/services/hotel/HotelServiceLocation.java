package com.trading212.weathertrip.services.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.hotel.HotelLocationDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;


@Service
public class HotelServiceLocation {
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public HotelServiceLocation(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public String findDestinationId(String city) throws JsonProcessingException {
        String url = BOOKING_LOCATION_URL + city;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, BOOKING_API_KEY);
        headers.set(RAPID_API_HOST, BOOKING_API_HOST);

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = response.getBody();
        HotelLocationDTO[] locations = objectMapper.readValue(body, new TypeReference<HotelLocationDTO[]>() {
        });
        return locations[0].getDestId();
    }
}
