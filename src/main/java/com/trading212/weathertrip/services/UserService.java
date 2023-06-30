package com.trading212.weathertrip.services;


import ch.qos.logback.core.testUtil.RandomUtil;
import com.trading212.weathertrip.controllers.errors.EmailAlreadyUsedException;
import com.trading212.weathertrip.controllers.errors.InvalidPasswordException;
import com.trading212.weathertrip.controllers.errors.UserNotFoundException;
import com.trading212.weathertrip.controllers.errors.UsernameAlreadyUsedException;
import com.trading212.weathertrip.controllers.validation.ChangePasswordValidation;
import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.domain.dto.UserDTO;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthService authService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authService = authService;
    }

    public User registerUser(RegisterUserValidation validation) {
        if (this.userRepository.findByEmail(validation.getEmail()).isPresent()) {
            throw new EmailAlreadyUsedException("Email is already used!");
        }
        if (this.userRepository.findByUsername(validation.getUsername()).isPresent()) {
            throw new UsernameAlreadyUsedException("Username is already used!");
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

    public Optional<User> requestForResetPassword(String email) {

        return userRepository.findByEmail(email).map(userToUpdate -> {
            userToUpdate.setResetDate(LocalDateTime.now());

            String key = RandomStringUtils.random(20, 0, 0, true, true, null);
            userToUpdate.setResetKey(key);

            userRepository.updateResetKeyAndDate(userToUpdate);

            return userToUpdate;
        });
    }

    public Optional<User> completePasswordReset(String newPassword, String key) {
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

    public Optional<User> activateAccount(String key) {
        return userRepository.findByActivationKey(key).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);

            userRepository.saveUpdateActivation(user);
            return user;
        });
    }
}
