package com.trading212.weathertrip.domain.constants;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Application constants.
 */
public final class Constants {

    public static final String PROFILE_DOMAIN = "http://localhost:3000";
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

    public static final List<String> GET_CITIES = List.of("Los Angeles", "Nice", "Florence", "Alanya", "Malaga", "Rio de Janeiro");
}
