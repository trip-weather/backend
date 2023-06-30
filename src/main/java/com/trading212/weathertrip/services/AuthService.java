package com.trading212.weathertrip.services;

import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getAuthenticatedUser() {
        if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof UserDetails details) {
            return this.userRepository.findByUsername(details.getUsername()).orElse(null);
        }
        return null;
    }
}
