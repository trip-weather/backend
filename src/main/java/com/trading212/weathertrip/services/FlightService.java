package com.trading212.weathertrip.services;

import com.duffel.DuffelApiClient;
import com.duffel.model.CabinClass;
import com.duffel.model.Passenger;
import com.duffel.model.PassengerType;
import com.duffel.model.request.OfferRequest;
import com.duffel.model.response.OfferResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.controllers.errors.InvalidFlightException;
import com.trading212.weathertrip.controllers.validation.FlightValidationDTO;
import com.trading212.weathertrip.domain.dto.flight.Airport;
import com.trading212.weathertrip.domain.dto.flight.AirportWrapperDTO;
import com.trading212.weathertrip.domain.dto.flight.FlightResponseWrapper;
import com.trading212.weathertrip.domain.entities.Flight;
import com.trading212.weathertrip.repositories.FlightRepository;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;

@Service
public class FlightService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final FlightRepository flightRepository;

    public FlightService(RestTemplate restTemplate, ObjectMapper objectMapper, FlightRepository flightRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.flightRepository = flightRepository;
    }

    public FlightResponseWrapper searchFlights(FlightValidationDTO validation) throws JsonProcessingException {
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String id = searchFlightId(validation);
        String url = DUFFEL_FIND_FLIGHTS_BY_OFFER_URL + id;

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        return objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });
    }

    private List<Airport> findAirportsByCity(String city) throws JsonProcessingException {
        HttpHeaders headers = getHttpHeaders();
        HttpEntity<Object> requestEntity = new HttpEntity<>(headers);

        String url = DUFFEL_FIND_AIRPORT_BY_CITY_NAME + city;
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

        AirportWrapperDTO airports = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });

        return airports.getAirports().stream()
                .filter(airport -> airport.getType().equals("airport") && airport.getCityName().equals(city))
                .collect(Collectors.toList());
    }

    private String searchFlightId(FlightValidationDTO validation) throws JsonProcessingException {
        List<Airport> originAirports = findAirportsByCity(validation.getOrigin());
        List<Airport> destinationAirports = findAirportsByCity(validation.getDestination());

        if (originAirports.size() == 0 || destinationAirports.size() == 0) {
            throw new InvalidFlightException("There is no flights found for this days : "
                    + validation.getDepartDate() + " - " + validation.getReturnDate());
        }

        OfferRequest.Slice outboundSlice = new OfferRequest.Slice();
        outboundSlice.setDepartureDate(validation.getDepartDate());
        outboundSlice.setOrigin(originAirports.get(0).getIataCode());
        outboundSlice.setDestination(destinationAirports.get(0).getIataCode());

        OfferRequest.Slice inboundSlice = new OfferRequest.Slice();
        inboundSlice.setDepartureDate(validation.getReturnDate());
        inboundSlice.setOrigin(destinationAirports.get(0).getIataCode());
        inboundSlice.setDestination(originAirports.get(0).getIataCode());

        Passenger passenger = new Passenger();
        passenger.setType(PassengerType.adult);
        passenger.setGivenName("Test");
        passenger.setFamilyName("User");

        OfferRequest request = new OfferRequest();
        request.setMaxConnections(0);
        request.setCabinClass(CabinClass.economy);
        request.setSlices(List.of(outboundSlice, inboundSlice));
        request.setPassengers(List.of(passenger, passenger));

        DuffelApiClient client = new DuffelApiClient(DUFFEL_API_KEY);
        OfferResponse offerResponse = client.offerRequestService.post(request);

        return offerResponse.getId();
    }

    private static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + DUFFEL_API_KEY);
        headers.set("Duffel-Version", "v1");
        return headers;
    }

    public void save(Flight flight) {
        flightRepository.save(flight);
    }
}
