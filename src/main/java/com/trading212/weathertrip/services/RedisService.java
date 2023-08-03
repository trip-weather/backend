package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.dto.GooglePlacesResultDTO;
import com.trading212.weathertrip.domain.dto.HotelInfo;
import com.trading212.weathertrip.domain.dto.weather.ForecastDTO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RedisService {
    private static final String HOTEL_INFORMATION_HASH_KEY = "hotel_information";
    private static final String HOTEL_NEARBY_HASH_KEY = "nearby_places";
    private static final String FORECAST_HASH_KEY = "city_forecast";

    private final HashOperations<String, String, Map<String, List<GooglePlacesResultDTO>>> hotelNearbyHashOperations;
    private final HashOperations<String, String, HotelInfo> hotelInfoHashOperations;
    private final HashOperations<String, String, List<ForecastDTO>> forecastHashOperations;

    public RedisService(@Qualifier("hotelNearbyPlaces") HashOperations<String, String, Map<String, List<GooglePlacesResultDTO>>> hotelNearbyHashOperations,
                        @Qualifier("hotelInfoHashOperations") HashOperations<String, String, HotelInfo> hotelInfoHashOperations,
                        @Qualifier("weatherHashOperations") HashOperations<String, String, List<ForecastDTO>> forecastHashOperations) {
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

    public Map<String, List<GooglePlacesResultDTO>> getNearbyPlaces(Integer externalId) {
        return hotelNearbyHashOperations.get(HOTEL_NEARBY_HASH_KEY, String.valueOf(externalId));
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

    public List<ForecastDTO> getWeatherData(String city) {
        return forecastHashOperations.get(FORECAST_HASH_KEY, city);
    }

    public void deleteAllFromHotelInfoHash() {
        try (Jedis jedis = new Jedis("localhost", 6379)) {
            jedis.del(HOTEL_INFORMATION_HASH_KEY);
        }
    }
}
