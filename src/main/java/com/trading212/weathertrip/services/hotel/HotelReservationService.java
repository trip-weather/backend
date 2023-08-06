package com.trading212.weathertrip.services.hotel;

import com.trading212.weathertrip.controllers.errors.InvalidOrderException;
import com.trading212.weathertrip.domain.dto.hotel.HotelReservationDTO;
import com.trading212.weathertrip.domain.dto.UserReservedHotelsDTO;
import com.trading212.weathertrip.domain.dto.hotel.HotelReservationDates;
import com.trading212.weathertrip.domain.entities.HotelReservation;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.HotelReservationRepository;
import com.trading212.weathertrip.services.AuthService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HotelReservationService {
    private final AuthService authService;
    private final HotelReservationRepository reservationRepository;

    public HotelReservationService(AuthService authService, HotelReservationRepository reservationRepository) {
        this.authService = authService;
        this.reservationRepository = reservationRepository;
    }

    public void save(HotelReservation reservation) {
        reservationRepository.save(reservation);
    }

    public HotelReservationDTO getReservationByOrderUuid(String uuid) {
        return reservationRepository.
                getReservationByOrderUuid(uuid).
                orElseThrow(() -> new InvalidOrderException("Reservation not found for order " + uuid));
    }

    public List<UserReservedHotelsDTO> getUserReservedHotels(String status) {
        User authUser = authService.getAuthenticatedUser();
        return reservationRepository.getUserReservedHotelsByStatus(authUser.getUuid(), status);
    }

    public List<Integer> getUserReservedHotelsIds() {
        User authUser = authService.getAuthenticatedUser();
        return reservationRepository.getUserReservedHotelsIds(authUser.getUuid());
    }


    public HotelReservationDates getUserReservedHotelDatesByExternalId(Integer externalId) {
        User authUser = authService.getAuthenticatedUser();
        return reservationRepository.getUserReservedHotelDatesByExternalId(externalId, authUser.getUuid());
    }
}
