package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.dto.GooglePlacesResultDTO;
import com.trading212.weathertrip.domain.dto.HotelInfo;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RedisService {
    private static final String HOTEL_INFORMATION_HASH_KEY = "hotel_information";
    private static final String HOTEL_NEARBY_HASH_KEY = "nearby_places";
    private static final String FORECAST_HASH_KEY = "city_forecast";
    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Map<String, List<GooglePlacesResultDTO>>> hotelNearbyHashOperations;
    private final HashOperations<String, String, HotelInfo> hotelInfoHashOperations;
    private final HashOperations<String, String, List<ForecastDTO>> forecastHashOperations;

    public RedisService(RedisTemplate<String, Object> redisTemplate,
                        @Qualifier("hotelNearbyPlaces") HashOperations<String, String, Map<String, List<GooglePlacesResultDTO>>> hotelNearbyHashOperations,
                        @Qualifier("hotelInfoHashOperations") HashOperations<String, String, HotelInfo> hotelInfoHashOperations,
                        @Qualifier("weatherHashOperations") HashOperations<String, String, List<ForecastDTO>> forecastHashOperations) {
        this.redisTemplate = redisTemplate;
        this.hotelNearbyHashOperations = hotelNearbyHashOperations;
        this.hotelInfoHashOperations = hotelInfoHashOperations;
        this.forecastHashOperations = forecastHashOperations;
    }

    public void saveNearby(Integer hotelId, String type, List<GooglePlacesResultDTO> places) {
        Map<String, List<GooglePlacesResultDTO>> map = hotelNearbyHashOperations.get(HOTEL_NEARBY_HASH_KEY, String.valueOf(hotelId));
        if (map == null) {
            map = new HashMap<>();
        }

        if (!map.containsKey(type)) {
            map.put(type, places);
            hotelNearbyHashOperations.put(HOTEL_NEARBY_HASH_KEY, String.valueOf(hotelId), map);
        }
    }

    public List<GooglePlacesResultDTO> getNearbyPlacesByType(Integer hotelId, String type) {
        Map<String, List<GooglePlacesResultDTO>> nearbyPlacesMap =
                hotelNearbyHashOperations.get(HOTEL_NEARBY_HASH_KEY, String.valueOf(hotelId));
        if (nearbyPlacesMap != null) {
            return nearbyPlacesMap.get(type);
        }
        return null;
    }

    public void saveHotelInfo(Integer externalId, HotelInfo information) {
        hotelInfoHashOperations.put(HOTEL_INFORMATION_HASH_KEY, String.valueOf(externalId), information);
    }

    public Map<String, HotelInfo> getAllHotelInfoSaved() {
        return hotelInfoHashOperations.entries(HOTEL_NEARBY_HASH_KEY);
    }

    public HotelInfo getHotelInformation(Integer externalId) {
        return hotelInfoHashOperations.get(HOTEL_INFORMATION_HASH_KEY, String.valueOf(externalId));
    }

    public void saveForecast(String city, List<ForecastDTO> forecast) {
        forecastHashOperations.put(FORECAST_HASH_KEY, city, forecast);
    }

    public List<ForecastDTO> getForecastForCity(String city) {
        return forecastHashOperations.get(FORECAST_HASH_KEY, city);
    }

    public List<String> getAllCitiesFromForecast() {
        Set<Object> keys = redisTemplate.opsForHash().keys(FORECAST_HASH_KEY);
        return keys.stream()
                .filter(key -> key instanceof String)
                .map(key -> (String) key)
                .collect(Collectors.toList());
    }

    public void updateForecast(String city, List<ForecastDTO> forecast) {
        forecastHashOperations.put(FORECAST_HASH_KEY, city, forecast);
    }
}
