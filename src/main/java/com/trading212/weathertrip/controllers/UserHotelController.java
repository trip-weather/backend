package com.trading212.weathertrip.controllers;

import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.services.AuthService;
import com.trading212.weathertrip.services.UserHotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/")
public class UserHotelController {
    private final AuthService authService;
    private final UserHotelService userHotelService;

    public UserHotelController(AuthService authService,
                               UserHotelService userHotelService) {
        this.authService = authService;
        this.userHotelService = userHotelService;
    }


    @PostMapping("/hotel/{id}/like")
    public ResponseEntity addHotelToFavourite(@PathVariable("id") Integer externalId) {
        User user = authService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (userHotelService.addToFavourite(externalId, user)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @DeleteMapping("/hotel/{id}/unlike")
    public ResponseEntity removeHotelFromFavourite(@PathVariable("id") Integer externalId) {
        User user = authService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (userHotelService.removeFromFavourite(externalId, user)) {
            return ResponseEntity.status(HttpStatus.OK).build();
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    @GetMapping("/user/favouriteHotels")
    public ResponseEntity<List<Integer>> getUserFavouriteHotels() {

        User user = authService.getAuthenticatedUser();
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<Integer> userFavouriteHotelsIds = userHotelService.getUserFavouriteHotelsIds(user.getUuid());

        return ResponseEntity.ok(userFavouriteHotelsIds);
    }
}
