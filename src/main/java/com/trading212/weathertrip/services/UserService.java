package com.trading212.weathertrip.services;


import com.trading212.weathertrip.controllers.errors.InvalidPasswordException;
import com.trading212.weathertrip.controllers.validation.ChangePasswordValidation;
import com.trading212.weathertrip.controllers.validation.RegisterUserValidation;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

    public void registerUser(RegisterUserValidation validation) {
//        if (this.userRepository.findByEmail(validation.getEmail()).isPresent()) {
//            throw new EmailAlreadyUsedException("Email is already used!");
//        }
//        if (this.userRepository.findByUsername(validation.getUsername()).isPresent()) {
//            throw new UsernameAlreadyUsedException("Username is already used!");
//        }

        String encryptedPassword = passwordEncoder.encode(validation.getPassword());
        userRepository.save(validation, encryptedPassword);
    }

    public void changePassword(ChangePasswordValidation validation) {
        User authUser = authService.getAuthenticatedUser();
        if (authUser != null) {
            String currentEncryptedPassword = authUser.getPassword();
            if (!passwordEncoder.matches(validation.getOldPassword(), currentEncryptedPassword)) {
                System.out.println("incorrect pass");
                throw new InvalidPasswordException("Incorrect password");
            }
            String encryptedPassword = passwordEncoder.encode(validation.getNewPassword());
            userRepository.updatePassword(authUser.getUuid(), encryptedPassword);

            log.info("Changed password for User: {} ", authUser);
        }
    }
}
