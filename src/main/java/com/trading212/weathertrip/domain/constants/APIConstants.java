package com.trading212.weathertrip.domain.constants;

/**
 * Third party API constants.
 */
public class APIConstants {
    public static final String WEATHER_LOCATION_URL = "https://ai-weather-by-meteosource.p.rapidapi.com/find_places?text=";
    public static final String RAPID_API_KEY = "X-RapidAPI-Key";
    public static final String WEATHER_API_KEY = "abd4f30257mshbd8e42cf282e630p13465fjsn2751064e4943";
    public static final String RAPID_API_HOST = "X-RapidAPI-Host";
    public static final String WEATHER_API_HOST = "ai-weather-by-meteosource.p.rapidapi.com";
    public static final String FORECAST_URL = "https://ai-weather-by-meteosource.p.rapidapi.com/daily?units=auto&place_id=";

    public static final String BOOKING_API_KEY = "f885ca8f1dmsh0da9f6fc361ea5cp17160cjsnfb1ec8a9b8c4";
    public static final String BOOKING_API_HOST = "booking-com.p.rapidapi.com";

    public static final String BOOKING_LOCATION_URL = "https://booking-com.p.rapidapi.com/v1/hotels/locations?locale=en-gb&name=";
    public static final String BOOKING_GET_HOTEL_DETAILS_URL = "https://booking-com.p.rapidapi.com/v2/hotels/details?currency=EUR&locale=en-gb&checkout_date=";
    public static final String BOOKING_HOTEL_PHOTOS_URL = "https://booking-com.p.rapidapi.com/v1/hotels/photos?locale=en-gb&hotel_id=";
    public static final String BOOKING_HOTEL_DESCRIPTION_URL = "https://booking-com.p.rapidapi.com/v1/hotels/description?locale=en-gb&hotel_id=";

    public static final String HOTEL_SEARCH_URL = "https://booking-com.p.rapidapi.com/v2/hotels/search?order_by=popularity&adults_number=2&checkin_date=";
    public static final String DUFFEL_API_KEY = "duffel_test_bIraT-Wxyat1qVtr0cAkARxXtVlL515V-SvCC-nAynJ";
    public static final String DUFFEL_FIND_AIRPORT_BY_CITY_NAME = "https://api.duffel.com/places/suggestions?name=";
    public static final String DUFFEL_FIND_FLIGHTS_BY_OFFER_URL = "https://api.duffel.com/air/offers?limit=5&sort=total_amount&max_connections=2&offer_request_id=";
    public static final String STRIPE_API_KEY = "sk_test_51NXn6mJAyJlvQQVyByoBNiBSMDOhqgRmHlLYnCBO5dYIrjSJSrLpfyUFzstiSSJIErCftbz7yDOdanuN5uPJSu65007LHHmPEk";
    public static String STRIPE_ENDPOINT_SECRET = "whsec_63cf9dbcdff16b197fbc412a8dd45531eba740dd449aac4edadf417c88b56255";
}
