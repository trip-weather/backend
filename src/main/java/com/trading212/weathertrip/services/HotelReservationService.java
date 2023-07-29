package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.InvalidOrderException;
import com.trading212.weathertrip.domain.dto.HotelReservationDTO;
import com.trading212.weathertrip.domain.entities.HotelReservation;
import com.trading212.weathertrip.repositories.HotelReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class HotelReservationService {
    private final HotelReservationRepository reservationRepository;

    public HotelReservationService(HotelReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void save(HotelReservation reservation) {
        reservationRepository.save(reservation);
    }

    public HotelReservationDTO getReservationByOrderUuid(String uuid) {
        return reservationRepository.
                getReservationByOrderUuid(uuid).
                orElseThrow(() -> new InvalidOrderException("Reservation not found"));
    }
}
