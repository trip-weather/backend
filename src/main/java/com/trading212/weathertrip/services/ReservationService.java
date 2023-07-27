package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.InvalidOrderException;
import com.trading212.weathertrip.domain.dto.ReservationDTO;
import com.trading212.weathertrip.domain.entities.Reservation;
import com.trading212.weathertrip.repositories.ReservationRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservationService {
    private final ReservationRepository reservationRepository;

    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public void save(Reservation reservation) {
        reservationRepository.save(reservation);
    }

    public ReservationDTO getReservationByOrderUuid(String uuid) {
        return reservationRepository.
                getReservationByOrderUuid(uuid).
                orElseThrow(() -> new InvalidOrderException("Reservation not found"));
    }
}
