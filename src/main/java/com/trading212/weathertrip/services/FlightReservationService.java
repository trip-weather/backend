package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.entities.FlightReservation;
import com.trading212.weathertrip.repositories.FlightReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class FlightReservationService {
    private final FlightReservationRepository flightReservationRepository;

    public FlightReservationService(FlightReservationRepository flightReservationRepository) {
        this.flightReservationRepository = flightReservationRepository;
    }

    public void save(FlightReservation flightReservation) {
        flightReservationRepository.save(flightReservation);
    }
}
