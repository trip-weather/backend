package com.trading212.weathertrip.domain.entities;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class UserFavouriteHotel {
    private int userId;
    private int hotelId;
    private LocalDateTime added_at;
}
