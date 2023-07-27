package com.trading212.weathertrip.services.hotel;

import com.trading212.weathertrip.controllers.errors.InvalidUserException;
import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import com.trading212.weathertrip.domain.entities.Hotel;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.HotelRepository;
import com.trading212.weathertrip.repositories.UserHotelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserHotelService {
    private final HotelService hotelService;
    private final HotelRepository hotelRepository;
    private final UserHotelRepository userHotelRepository;

    public UserHotelService(HotelService hotelService,
                            HotelRepository hotelRepository,
                            UserHotelRepository userHotelRepository) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
        this.userHotelRepository = userHotelRepository;
    }

    public boolean addToFavourite(Integer externalId, User user) {
        Hotel hotel = hotelService.findByExternalId(externalId);
        hotel.setFavouriteCount(hotel.getFavouriteCount() + 1);

        if (userHotelRepository.isHotelInUserFavorites(user.getUuid(), hotel.getUuid())) {
            throw new InvalidUserException("Hotel with UUID " + hotel.getUuid() +
                    " is already added to the favorites for User with UUID " + user.getUuid());
        }

        boolean hotelUpdateSuccess = hotelRepository.updateFavouriteCount(hotel);
        boolean userHotelSaveSuccess = userHotelRepository.save(user.getUuid(), hotel.getUuid());

        return hotelUpdateSuccess && userHotelSaveSuccess;
    }

    public boolean removeFromFavourite(Integer externalId, User user) {
        Hotel hotel = hotelService.findByExternalId(externalId);
        hotel.setFavouriteCount(hotel.getFavouriteCount() - 1);

        boolean hotelUpdateSuccess = hotelRepository.updateFavouriteCount(hotel);
        boolean hotelRemoveSuccess = userHotelRepository.delete(user.getUuid(), hotel.getUuid());

        return hotelUpdateSuccess && hotelRemoveSuccess;
    }

    public List<FavouriteHotelDTO> getUserFavouriteHotels(String uuid) {
        return userHotelRepository.getUserFavouriteHotels(uuid);
    }

    public List<Integer> getUserFavouriteHotelsIds(String uuid) {
        return userHotelRepository.getUserFavouriteHotelsIds(uuid);
    }
}
