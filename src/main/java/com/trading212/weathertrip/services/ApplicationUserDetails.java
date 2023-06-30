package com.trading212.weathertrip.services;

import com.trading212.weathertrip.controllers.errors.UserNotFoundException;
import com.trading212.weathertrip.domain.entities.Authority;
import com.trading212.weathertrip.domain.entities.User;
import com.trading212.weathertrip.repositories.UserRepository;
import com.trading212.weathertrip.repositories.UserRoleRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApplicationUserDetails implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;

    public ApplicationUserDetails(UserRepository userRepository, UserRoleRepository userRoleRepository) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User with name " + username + "not found !");
        }
        return map(user.get());

//        return userRepository.findByUsername(username)
//                .map(this::map)
//                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + " not found !"));
    }

    private UserDetails map(User user) {
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                extractAuthorities(user)
        );
    }

    private List<GrantedAuthority> extractAuthorities(User user) {
        return user.getAuthorities()
                .stream()
                .map(this::mapRole)
                .collect(Collectors.toList());
    }

    private GrantedAuthority mapRole(Authority authority) {
        return new SimpleGrantedAuthority(authority.getName());
    }
}
