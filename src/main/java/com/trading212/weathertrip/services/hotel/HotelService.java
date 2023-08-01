package com.trading212.weathertrip.services.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.controllers.errors.HotelNotFoundException;
import com.trading212.weathertrip.domain.dto.HotelInfo;
import com.trading212.weathertrip.domain.dto.hotel.*;
import com.trading212.weathertrip.domain.dto.hotelDetailsData.HotelData;
import com.trading212.weathertrip.domain.entities.Hotel;
import com.trading212.weathertrip.repositories.HotelRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;
import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
public class HotelService {
    private static final String HASH_KEY = "hotel_information";
    private final RestTemplate restTemplate;
    private final HotelServiceLocation hotelServiceLocation;
    private final HotelRepository hotelRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final HashOperations<String, String, HotelInfo> hotelInfoHashOperations;

    public HotelService(RestTemplate restTemplate,
                        HotelServiceLocation hotelServiceLocation,
                        HotelRepository hotelRepository,
                        ObjectMapper objectMapper,
                        ModelMapper modelMapper,
                        @Qualifier("hotelInfoHashOperations") HashOperations<String, String, HotelInfo> hotelInfoHashOperations) {
        this.restTemplate = restTemplate;
        this.hotelServiceLocation = hotelServiceLocation;
        this.hotelRepository = hotelRepository;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.hotelInfoHashOperations = hotelInfoHashOperations;
    }

    public void save(List<WrapperHotelDTO> hotels) {
        hotelRepository.save(hotels);
    }

    public List<WrapperHotelDTO> findAvailableHotels(Map<LocalDate, LocalDate> periods, String cityName) throws IOException {
        String destinationId = getDestinationId(cityName);
        HttpEntity<Object> requestEntity = getRequestEntity();

        List<WrapperHotelDTO> result = new ArrayList<>();

        for (Map.Entry<LocalDate, LocalDate> period : periods.entrySet()) {
            String url = HOTEL_SEARCH_URL
                    + period.getKey() + "&filter_by_currency=EUR&dest_id="
                    + destinationId + "&locale=en-gb&checkout_date="
                    + period.getValue() + "&units=metric&room_number=1&dest_type=city";

            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);

            String body = response.getBody();
            WrapperHotelDTO hotels = objectMapper.readValue(body, new TypeReference<WrapperHotelDTO>() {
            });

            List<HotelResultDTO> sorted =
                    hotels.getResults()
                            .stream()
                            .sorted(Comparator.comparingInt(HotelResultDTO::getPropertyClass).reversed())
                            .collect(Collectors.toList());

            result.add(new WrapperHotelDTO(sorted));
        }

//
//// Only for testing
//        String jsonFilePath = "src/main/resources/hotel_in_sofia_data.json";
//        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
//
//        List<WrapperHotelDTO> hotels = objectMapper.readValue(jsonContent, new TypeReference<List<WrapperHotelDTO>>() {
//        });

        return result;
    }

    public List<HotelResultDTO> findAvailableHotelsForAllCities(Map<LocalDate, LocalDate> periods) throws JsonProcessingException {
        List<HotelResultDTO> result = new ArrayList<>();
        HttpEntity<Object> requestEntity = getRequestEntity();

        for (String cityName : GET_CITIES) {
            String destinationId = getDestinationId(cityName);
            for (Map.Entry<LocalDate, LocalDate> period : periods.entrySet()) {
                String url = HOTEL_SEARCH_URL
                        + period.getKey() + "&filter_by_currency=EUR&dest_id="
                        + destinationId + "&locale=en-gb&checkout_date="
                        + period.getValue() + "&units=metric&room_number=1&dest_type=city&rows=4";

                getResponse(requestEntity, result, url);
            }
        }
        return result;
    }

    public List<HotelResultDTO> getHotels() throws IOException {
        ArrayList<String> destinationIds = getDestinationIds();
        HttpEntity<Object> requestEntity = getRequestEntity();

        List<HotelResultDTO> result = new ArrayList<>();

        for (String destinationId : destinationIds) {
            String url = HOTEL_SEARCH_URL
                    + DEFAULT_START_DATE + "&filter_by_currency=EUR&dest_id="
                    + destinationId + "&locale=en-gb&checkout_date="
                    + DEFAULT_END_DATE + "&units=metric&room_number=1&dest_type=city";
            getResponse(requestEntity, result, url);
        }

//         only for testing
//        String jsonFilePath = "src/main/resources/hotel_in_sofia_data.json";
//        String jsonContent = new String(Files.readAllBytes(Paths.get(jsonFilePath)));
//
//        List<WrapperHotelDTO> hotels = objectMapper.readValue(jsonContent, new TypeReference<List<WrapperHotelDTO>>() {
//        });
//        return hotels.get(0).getResults();

        return result;
    }


    public Hotel findByExternalId(Integer externalId) {
        return hotelRepository
                .findByExternalId(externalId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with externalId " + externalId + " not found"));
    }

    public Hotel findByUuid(String uuid) {
        return hotelRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with uuid " + uuid + " not found"));
    }

    public HotelDetailsDTO getHotel(Integer externalId, String checkInDate, String checkOutDate) throws JsonProcessingException {
        // TODO make request to reservations

        Hotel hotel = hotelRepository
                .findByExternalId(externalId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with externalId " + externalId + " not found"));


        HotelInfo information = getHotelInformation(externalId);

        if (information == null) {
            List<HotelPhoto> photos = getHotelPhotos(externalId);
            HotelData data = getHotelData(externalId, checkInDate, checkOutDate);
            String description = getHotelDescriptionFromAPI(externalId);

            information = HotelInfo.builder()
                    .data(data)
                    .photos(photos)
                    .description(description)
                    .build();

            saveHotelInfo(externalId, information);
        }

        double pricePerDay = information.getData().getPrice().getPricePerNight().getValue();
        String arrivalDate = information.getData().getArrivalDate();
        String departureDate = information.getData().getDepartureDate();

        int nights = (int) ChronoUnit.DAYS.between(LocalDate.parse(arrivalDate), LocalDate.parse(departureDate));

        HotelDetailsDTO hotelDetailsDTO = modelMapper.map(information.getData(), HotelDetailsDTO.class);

        hotelDetailsDTO.setPricePerDay(pricePerDay);
        hotelDetailsDTO.setTotalPrice(pricePerDay * nights);
        hotelDetailsDTO.setNights(nights);
        hotelDetailsDTO.setPhotos(information.getPhotos());
        hotelDetailsDTO.setDescription(information.getDescription());
        hotelDetailsDTO.setFavouriteCount(hotel.getFavouriteCount());
        return hotelDetailsDTO;
    }


    public HotelData getHotelData(Integer externalId, String checkInDate, String checkOutDate) throws JsonProcessingException {
        String url = BOOKING_GET_HOTEL_DETAILS_URL + checkOutDate + "&checkin_date=" + checkInDate + "&hotel_id=" + externalId;

        String body = getBody(url);
        return objectMapper.readValue(body, new TypeReference<>() {
        });

    }

    private List<HotelPhoto> getHotelPhotos(Integer externalId) throws JsonProcessingException {
        String url = BOOKING_HOTEL_PHOTOS_URL + externalId;

        String body = getBody(url);
        return objectMapper.readValue(body, new TypeReference<>() {
        });
    }

    private String getHotelDescriptionFromAPI(Integer externalId) throws JsonProcessingException {
        String url = BOOKING_HOTEL_DESCRIPTION_URL + externalId;

        String body = getBody(url);
        HotelDescription hotelDescription = objectMapper.readValue(body, new TypeReference<>() {
        });

        return hotelDescription.getDescription();
    }

    private String getBody(String url) {
        HttpEntity<Object> requestEntity = getRequestEntity();

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        return response.getBody();
    }

    private ArrayList<String> getDestinationIds() throws JsonProcessingException {
        ArrayList<String> destinationIds = new ArrayList<>();
        for (String city : GET_CITIES) {
            destinationIds.add(getDestinationId(city));
        }
        return destinationIds;
    }

    private String getDestinationId(String cityName) throws JsonProcessingException {
        return hotelServiceLocation.findDestinationId(cityName);
    }

    private void getResponse(HttpEntity<Object> requestEntity, List<HotelResultDTO> result, String url) throws JsonProcessingException {
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String body = response.getBody();
        WrapperHotelDTO hotels = objectMapper.readValue(body, new TypeReference<WrapperHotelDTO>() {
        });

        List<HotelResultDTO> limited =
                hotels.getResults()
                        .stream()
                        .sorted(Comparator.comparingInt(HotelResultDTO::getPropertyClass).reversed())
                        .limit(5).toList();
        result.addAll(limited);
    }

    private HttpEntity<Object> getRequestEntity() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(RAPID_API_KEY, BOOKING_API_KEY);
        headers.set(RAPID_API_HOST, BOOKING_API_HOST);
        return new HttpEntity<>(headers);
    }

    public void saveHotelInfo(Integer externalId, HotelInfo information) {
        hotelInfoHashOperations.put(HASH_KEY, String.valueOf(externalId), information);
    }

    private HotelInfo getHotelInformation(Integer externalId) {
        return hotelInfoHashOperations.get(HASH_KEY, String.valueOf(externalId));
    }
}
