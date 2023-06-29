package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return this.userRepository.findByUsername(username).orElse(null);
    }
}
