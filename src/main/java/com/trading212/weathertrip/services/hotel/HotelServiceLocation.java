package com.trading212.weathertrip.services.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.constants.Constants;
import com.trading212.weathertrip.domain.dto.hotel.HotelLocationDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class HotelServiceLocation {
    private final RestTemplate restTemplate;

    public HotelServiceLocation(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public String findDestinationId(String city) throws JsonProcessingException {
        String url = Constants.BOOKING_LOCATION_URL + city;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(Constants.RAPID_API_KEY, Constants.BOOKING_API_KEY);
        headers.set(Constants.RAPID_API_HOST, Constants.BOOKING_API_HOST);

        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        String body = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        HotelLocationDTO[] locations = objectMapper.readValue(body, new TypeReference<HotelLocationDTO[]>() {
        });
        return locations[0].getDest_id();
    }
}
