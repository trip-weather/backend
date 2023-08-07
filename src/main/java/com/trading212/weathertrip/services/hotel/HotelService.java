package com.trading212.weathertrip.services.hotel;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.trading212.weathertrip.controllers.errors.HotelNotFoundException;
import com.trading212.weathertrip.domain.dto.GooglePlacesResultDTO;
import com.trading212.weathertrip.domain.dto.HotelInfo;
import com.trading212.weathertrip.domain.dto.hotel.*;
import com.trading212.weathertrip.domain.dto.hotelDetailsData.HotelData;
import com.trading212.weathertrip.domain.entities.Hotel;
import com.trading212.weathertrip.repositories.HotelRepository;
import com.trading212.weathertrip.services.GoogleMapsService;
import com.trading212.weathertrip.services.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.trading212.weathertrip.domain.constants.APIConstants.*;
import static com.trading212.weathertrip.domain.constants.Constants.*;

@Service
@Slf4j
public class HotelService {

    private final RestTemplate restTemplate;
    private final HotelServiceLocation hotelServiceLocation;
    private final HotelRepository hotelRepository;
    private final ObjectMapper objectMapper;
    private final ModelMapper modelMapper;
    private final GoogleMapsService googleMapsService;
    private final RedisService redisService;


    public HotelService(RestTemplate restTemplate,
                        HotelServiceLocation hotelServiceLocation,
                        HotelRepository hotelRepository,
                        ObjectMapper objectMapper,
                        ModelMapper modelMapper,
                        GoogleMapsService googleMapsService, RedisService redisService) {
        this.restTemplate = restTemplate;
        this.hotelServiceLocation = hotelServiceLocation;
        this.hotelRepository = hotelRepository;
        this.objectMapper = objectMapper;
        this.modelMapper = modelMapper;
        this.googleMapsService = googleMapsService;
        this.redisService = redisService;
    }

    public void save(List<WrapperHotelDTO> hotels) {
        hotelRepository.save(hotels);
    }

    public HotelDetailsDTO getHotel(Integer externalId, String checkInDate, String checkOutDate, List<String> nearby) throws JsonProcessingException {

        Hotel hotel = findByExternalId(externalId);
        HotelInfo information = getHotelInfo(externalId, checkInDate, checkOutDate);

        double pricePerDay = information.getData().getPrice().getPricePerNight().getValue();
        int nights = calculateNights(information.getData().getArrivalDate(), information.getData().getDepartureDate());

        HotelDetailsDTO hotelDetailsDTO = modelMapper.map(information.getData(), HotelDetailsDTO.class);
        hotelDetailsDTO.setPricePerDay(pricePerDay);
        hotelDetailsDTO.setTotalPrice(pricePerDay * nights);
        hotelDetailsDTO.setNights(nights);
        hotelDetailsDTO.setPhotos(information.getPhotos());
        hotelDetailsDTO.setDescription(information.getDescription());
        hotelDetailsDTO.setFavouriteCount(hotel.getFavouriteCount());

        if (nearby != null) {
            HashMap<String, List<GooglePlacesResultDTO>> hotelNearbyPlaces = new HashMap<>();
            for (String type : nearby) {
                List<GooglePlacesResultDTO> placesByType = redisService.getNearbyPlacesByType(externalId, type);
                hotelNearbyPlaces.put(type, placesByType);
            }
            hotelDetailsDTO.setNearby(hotelNearbyPlaces);
        }
        return hotelDetailsDTO;
    }

    public List<WrapperHotelDTO> findAvailableHotels(Map<LocalDate, LocalDate> periods, String cityName, List<String> nearby) throws IOException {
        log.info("Request for searching hotels!");
        String destinationId = getDestinationId(cityName);

        List<HotelResultDTO> allHotels = new ArrayList<>();
        for (Map.Entry<LocalDate, LocalDate> period : periods.entrySet()) {
            List<HotelResultDTO> hotelsForPeriod = searchHotelsForPeriod(destinationId, period.getKey(), period.getValue());
            allHotels.addAll(hotelsForPeriod);
        }

        if (nearby != null && !nearby.isEmpty()) {
            applyNearbyFilters(allHotels, nearby);
        }
        return List.of(new WrapperHotelDTO(allHotels));
    }

    public List<HotelResultDTO> findAvailableHotelsForAllCities(Map<LocalDate, LocalDate> periods) throws JsonProcessingException {
        List<HotelResultDTO> result = new ArrayList<>();
        HttpEntity<Object> requestEntity = getRequestEntity();

        Collections.shuffle(GET_CITIES);

        for (String cityName : GET_CITIES.subList(0, 3)) {
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

    public List<HotelResultDTO> getSuggestedHotels() throws IOException {
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
        return result;
    }


    public Hotel findByExternalId(Integer externalId) {
        return hotelRepository
                .findByExternalId(externalId)
                .orElseThrow(() -> new HotelNotFoundException("Hotel with externalId " + externalId + " not found"));
    }

    public void updateDescriptionAndPhotos() throws JsonProcessingException {
        Map<String, HotelInfo> oldHotelInfo = redisService.getAllHotelInfoSaved();

        for (Map.Entry<String, HotelInfo> entry : oldHotelInfo.entrySet()) {
            int id = Integer.parseInt(entry.getKey());

            List<HotelPhoto> photos = getHotelPhotos(id);
            String description = getHotelDescription(id);
            HotelData data = entry.getValue().getData();

            HotelInfo information = buildUpdatedHotelInfo(data, photos, description);
            redisService.saveHotelInfo(id, information);
        }
    }

    public void updateHotelData() throws JsonProcessingException {
        Map<String, HotelInfo> oldHotelInfo = redisService.getAllHotelInfoSaved();

        for (Map.Entry<String, HotelInfo> entry : oldHotelInfo.entrySet()) {
            int id = Integer.parseInt(entry.getKey());

            List<HotelPhoto> photos = entry.getValue().getPhotos();
            HotelData data = getHotelData(id, entry.getValue().getData().getArrivalDate(),
                    entry.getValue().getData().getDepartureDate());
            String description = entry.getValue().getDescription();

            HotelInfo information = buildUpdatedHotelInfo(data, photos, description);
            redisService.saveHotelInfo(id, information);
        }
    }

    private HotelInfo buildUpdatedHotelInfo(HotelData data, List<HotelPhoto> photos, String description) {
        return HotelInfo.builder()
                .data(data)
                .photos(photos)
                .description(description)
                .build();
    }

    private HotelData getHotelData(Integer externalId, String checkInDate, String checkOutDate) throws JsonProcessingException {
        String url = BOOKING_GET_HOTEL_DETAILS_URL + checkOutDate +
                "&checkin_date=" + checkInDate +
                "&hotel_id=" + externalId;

        String body = getBody(url);
        return objectMapper.readValue(body, new TypeReference<>() {
        });
    }

    private int calculateNights(String arrivalDate, String departureDate) {
        return (int) ChronoUnit.DAYS.between(LocalDate.parse(arrivalDate), LocalDate.parse(departureDate));
    }

    private HotelInfo getHotelInfo(Integer externalId, String checkInDate, String checkOutDate) throws JsonProcessingException {
        HotelInfo information = redisService.getHotelInformation(externalId);
        if (information == null) {
            List<HotelPhoto> photos = getHotelPhotos(externalId).stream().limit(15).toList();
            HotelData data = getHotelData(externalId, checkInDate, checkOutDate);
            String description = getHotelDescription(externalId);

            information = HotelInfo.builder()
                    .data(data)
                    .photos(photos)
                    .description(description)
                    .build();

            redisService.saveHotelInfo(externalId, information);
        }
        return information;
    }

    private List<HotelPhoto> getHotelPhotos(Integer externalId) throws JsonProcessingException {
        String url = BOOKING_HOTEL_PHOTOS_URL + externalId;

        String body = getBody(url);
        return objectMapper.readValue(body, new TypeReference<>() {
        });
    }

    private String getHotelDescription(Integer externalId) throws JsonProcessingException {
        String url = BOOKING_HOTEL_DESCRIPTION_URL + externalId;

        String body = getBody(url);
        HotelDescription hotelDescription = objectMapper.readValue(body, new TypeReference<>() {
        });

        return hotelDescription.getDescription();
    }

    private void applyNearbyFilters(List<HotelResultDTO> hotels, List<String> nearby) {
        for (HotelResultDTO hotel : hotels) {
            HashMap<String, Integer> nearbyFilters = new HashMap<>();
            for (String keyword : nearby) {
                List<GooglePlacesResultDTO> nearbyPlacesByType =
                        redisService.getNearbyPlacesByType(hotel.getId(), keyword);

                if (nearbyPlacesByType == null) {
                    List<GooglePlacesResultDTO> amenities = googleMapsService
                            .findNearest(hotel.getLatitude(), hotel.getLongitude(), keyword)
                            .stream()
                            .limit(10)
                            .collect(Collectors.toList());

                    redisService.saveNearby(hotel.getId(), keyword, amenities);
                    nearbyPlacesByType = amenities;
                }

                if (!nearbyPlacesByType.isEmpty()) {
                    nearbyFilters.put(keyword, nearbyPlacesByType.size());
                    hotel.setNearbyFilters(nearbyFilters);
                }
            }
        }
    }

    private List<HotelResultDTO> searchHotelsForPeriod(String destinationId, LocalDate checkInDate, LocalDate checkOutDate) throws JsonProcessingException {
        String url = HOTEL_SEARCH_URL
                + checkInDate + "&filter_by_currency=EUR" +
                "&dest_id=" + destinationId + "&locale=en-gb" +
                "&checkout_date=" + checkOutDate +
                "&units=metric&room_number=1&dest_type=city";

        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, getRequestEntity(), String.class);
        WrapperHotelDTO wrapperHotelDTO = objectMapper.readValue(response.getBody(), new TypeReference<>() {
        });
        return wrapperHotelDTO
                .getResults().stream()
                .sorted(Comparator.comparing(HotelResultDTO::getPropertyClass).reversed())
                .limit(12)
                .collect(Collectors.toList());
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
        WrapperHotelDTO hotels = objectMapper.readValue(body, new TypeReference<>() {
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

}
