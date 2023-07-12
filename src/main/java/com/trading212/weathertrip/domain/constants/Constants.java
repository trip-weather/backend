package com.trading212.weathertrip.domain.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Application constants.
 */
public final class Constants {

    public static final int PASSWORD_MIN_LENGTH = 4;
    public static final int PASSWORD_MAX_LENGTH = 50;
    public static final int USERNAME_MIN_SIZE = 4;
    public static final int USERNAME_MAX_SIZE = 20;
    public static final int FIRSTNAME_MIN_SIZE = 3;
    public static final int LASTNAME_MIN_SIZE = 3;
    public static final int DEFAULT_PERIOD = 5;
    public static final LocalDate DEFAULT_START_DATE = LocalDate.now();
    public static final LocalDate DEFAULT_END_DATE = LocalDate.now().plusDays(DEFAULT_PERIOD);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final String WEATHER_LOCATION_URL = "https://ai-weather-by-meteosource.p.rapidapi.com/find_places?text=";
    public static final String RAPID_API_KEY = "X-RapidAPI-Key";
    public static final String WEATHER_API_KEY = "c64ff6fea1msh678c4f26cf2708cp188304jsn49c64882facf";
    public static final String RAPID_API_HOST = "X-RapidAPI-Host";
    public static final String WEATHER_API_HOST = "ai-weather-by-meteosource.p.rapidapi.com";

    public static final String BOOKING_API_KEY = "ef22a23ccamsh426b32707d2c099p19d7acjsn2870201461bb";

    public static final String BOOKING_API_HOST = "booking-com.p.rapidapi.com";

    public static final String FORECAST_URL = "https://ai-weather-by-meteosource.p.rapidapi.com/daily?units=auto&place_id=";
    public static final String BOOKING_LOCATION_URL = "https://booking-com.p.rapidapi.com/v1/hotels/locations?locale=en-gb&name=";
    public static final String HOTEL_SEARCH_URL = "https://booking-com.p.rapidapi.com/v2/hotels/search?";
    public static final List<String> GET_CITIES = List.of("Sofia", "Plovdiv", "Florence", "Alanya");
}
