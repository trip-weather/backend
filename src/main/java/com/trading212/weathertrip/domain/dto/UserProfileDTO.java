package com.trading212.weathertrip.domain.dto;

import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserProfileDTO {
    private String userUuid;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;
    private List<FavouriteHotelDTO> favouriteHotels;
}
