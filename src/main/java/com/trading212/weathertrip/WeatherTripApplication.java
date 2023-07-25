package com.trading212.weathertrip;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WeatherTripApplication {

    public static void main(String[] args) {
        SpringApplication.run(WeatherTripApplication.class, args);
    }

}
