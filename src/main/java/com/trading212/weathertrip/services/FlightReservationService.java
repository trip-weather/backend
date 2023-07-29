package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.dto.flight.FlightReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedFlightsDTO;
import com.trading212.weathertrip.domain.entities.FlightReservation;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.FlightReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlightReservationService {
    private final FlightReservationRepository flightReservationRepository;
    private final AuthService authService;

    public FlightReservationService(FlightReservationRepository flightReservationRepository,
                                    AuthService authService) {
        this.flightReservationRepository = flightReservationRepository;
        this.authService = authService;
    }

    public void save(FlightReservation flightReservation) {
        flightReservationRepository.save(flightReservation);
    }

    public List<UserReservedFlightsDTO> getUserReservedFlights(String status) {
        User authUser = authService.getAuthenticatedUser();
        return flightReservationRepository.getUserReservedFlightsByStatus(authUser.getUuid(), status);
    }

    public List<FlightReservationDTO> getReservationByOrderUuid(String uuid) {
        return flightReservationRepository
                .getReservationByOrderUuid(uuid);
    }
}
