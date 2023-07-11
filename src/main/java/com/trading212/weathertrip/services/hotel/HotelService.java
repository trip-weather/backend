package com.trading212.weathertrip.services.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.domain.dto.hotel.WrapperHotelDTO;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.trading212.weathertrip.domain.constants.Constants.*;
import static com.trading212.weathertrip.domain.constants.Constants.BOOKING_API_HOST;

@Service
public class HotelService {
    private final RestTemplate restTemplate;
    private final HotelServiceLocation hotelServiceLocation;

    public HotelService(RestTemplate restTemplate, HotelServiceLocation hotelServiceLocation) {
        this.restTemplate = restTemplate;
        this.hotelServiceLocation = hotelServiceLocation;
    }

    public List<WrapperHotelDTO> findAvailableHotels(HashMap<LocalDate, LocalDate> periods, String cityName) throws JsonProcessingException {
        String destinationId = hotelServiceLocation.findDestinationId(cityName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, BOOKING_API_KEY);
        headers.set(RAPID_API_HOST, BOOKING_API_HOST);
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        List<WrapperHotelDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDate, LocalDate> period : periods.entrySet()) {
            String url = "https://booking-com.p.rapidapi.com/v2/hotels/search?order_by=price&adults_number=2&checkin_date=" + period.getKey() + "&filter_by_currency=AED&dest_id=" + destinationId + "&locale=en-gb&checkout_date=" + period.getValue() + "&units=metric&room_number=3&dest_type=city";
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            String body = response.getBody();
            ObjectMapper objectMapper = new ObjectMapper();
            WrapperHotelDTO hotels = objectMapper.readValue(body, new TypeReference<WrapperHotelDTO>() {
            });

            result.add(hotels);
        }
        return result;
    }
}
