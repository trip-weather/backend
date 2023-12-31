package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Flight {
    private String uuid;

    private String from;

    private String to;

    private String provider;

    private LocalDate departingAt;

    private LocalDate arrivingAt;

    @Override
    public String toString() {
        return "Flight{" +
                "uuid='" + uuid + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", provider='" + provider + '\'' +
                ", departingAt=" + departingAt +
                ", arrivingAt=" + arrivingAt +
                '}';
    }
}
