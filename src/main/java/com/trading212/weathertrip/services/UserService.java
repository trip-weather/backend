package com.trading212.weathertrip.services;


import com.trading212.weathertrip.controllers.errors.EmailAlreadyUsedException;
import com.trading212.weathertrip.controllers.errors.InvalidPasswordException;
import com.trading212.weathertrip.controllers.errors.UsernameAlreadyUsedException;
import com.trading212.weathertrip.controllers.validation.ChangePasswordValidation;
import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.controllers.validation.UpdateUserValidation;
import com.trading212.weathertrip.domain.dto.UserProfileDTO;
import com.trading212.weathertrip.domain.dto.hotel.FavouriteHotelDTO;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import com.trading212.weathertrip.services.hotel.UserHotelService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final UserHotelService userHotelService;
    private final ModelMapper modelMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService, UserHotelService userHotelService, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
        this.userHotelService = userHotelService;
        this.modelMapper = modelMapper;
    }

    public User registerUser(RegisterUserValidation validation) {
        if (!validation.getPassword().equals(validation.getRepeatedPassword())) {
            throw new InvalidPasswordException("Passwords must be same!");
        }
        if (this.userRepository.findByUsername(validation.getUsername()).isPresent()) {
            throw new UsernameAlreadyUsedException("Username is already used!");
        }

        if (this.userRepository.findByEmail(validation.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email is already used!");
        }

        String encryptedPassword = passwordEncoder.encode(validation.getPassword());
        String key = RandomStringUtils.random(20, 0, 0, true, true, null);

        User userToSave = User.builder()
                .username(validation.getUsername())
                .email(validation.getEmail())
                .firstName(validation.getFirstName())
                .lastName(validation.getLastName())
                .activated(false)
                .password(encryptedPassword)
                .activationKey(key)
                .createdDate(LocalDateTime.now())
                .build();

        return userRepository.save(userToSave);
    }

    @Transactional
    public void changePassword(ChangePasswordValidation validation) {
        User authUser = authService.getAuthenticatedUser();
        if (authUser != null) {
            String currentEncryptedPassword = authUser.getPassword();
            if (!passwordEncoder.matches(validation.getOldPassword(), currentEncryptedPassword)) {
                throw new InvalidPasswordException("Incorrect password");
            }
            String encryptedPassword = passwordEncoder.encode(validation.getNewPassword());
            userRepository.updatePassword(authUser.getUuid(), encryptedPassword);

            log.info("Changed password for User: {} ", authUser);
        }
    }

    @Transactional
    public Optional<User> requestForResetPassword(String email) {
        return userRepository.findByEmail(email).map(userToUpdate -> {
            userToUpdate.setResetDate(LocalDateTime.now());

            String key = RandomStringUtils.random(20, 0, 0, true, true, null);
            userToUpdate.setResetKey(key);

            userRepository.updateResetKeyAndDate(userToUpdate);
            return userToUpdate;
        });
    }

    @Transactional
    public Optional<User> completePasswordReset(String newPassword, String key) {
        log.info("Reset user password for reset key {}", key);
        return userRepository.findByResetKey(key)
                .filter(user -> user.getResetDate().isAfter(LocalDateTime.now().minus(1, ChronoUnit.DAYS)))
                .map(user -> {
                    user.setPassword(passwordEncoder.encode(newPassword));
                    user.setResetKey(null);
                    user.setResetDate(null);

                    userRepository.resetPassword(user);
                    return user;
                });
    }


    @Transactional
    public Optional<User> activateAccount(String key) {
        log.info("Activating user for activation key {}", key);
        return userRepository.findByActivationKey(key).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);

            userRepository.saveUpdateActivation(user);
            return user;
        });
    }

    public UserProfileDTO getUserProfileByUuid(String uuid) {
        User authUser = authService.getAuthenticatedUser();
        List<FavouriteHotelDTO> favouriteHotels =
                userHotelService
                        .getUserFavouriteHotels(uuid)
                        .stream().map(hotel -> modelMapper.map(hotel, FavouriteHotelDTO.class))
                        .toList();

        return UserProfileDTO.builder()
                .userUuid(authUser.getUuid())
                .email(authUser.getEmail())
                .username(authUser.getUsername())
                .password(authUser.getPassword())
                .firstName(authUser.getFirstName())
                .lastName(authUser.getLastName())
                .favouriteHotels(favouriteHotels)
                .build();
    }

    public void update(UpdateUserValidation validation, String uuid) {
        userRepository.updateUser(validation, uuid);
        log.info("Updated user with uuid : {}", uuid);
    }
}
