package com.trading212.weathertrip.services;

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
        System.out.println("load by username");

        try {
            System.out.println(userRepository.findByUsername(username).isEmpty() ? "empty user" : "full user");
            System.out.println(userRepository.findByUsername(username).get().getAuthorities());
        } catch (Exception exception) {
            System.out.println("User not found!");
        }

        User user = userRepository.findByUsername(username).get();
        user.setAuthorities(Set.of(userRoleRepository.findRoleUserUuid(user.getUuid()).get()));

        return map(user);

//        return userRepository.findByUsername(username)
//                .map(this::map)
//                .orElseThrow(() -> new UsernameNotFoundException("User with name " + username + " not found !"));
    }

    private UserDetails map(User user) {
        System.out.println(extractAuthorities(user).isEmpty() ? "empty authorities" : "full authorities");
        extractAuthorities(user).forEach(authority -> System.out.println(authority.getAuthority()));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                extractAuthorities(user)
        );
    }

    private List<GrantedAuthority> extractAuthorities(User user) {

        System.out.println(userRoleRepository.findRoleUserUuid(user.getUuid()));
        return user.getAuthorities()
                .stream()
                .map(this::mapRole)
                .collect(Collectors.toList());
    }

    private GrantedAuthority mapRole(Authority authority) {
        return new SimpleGrantedAuthority(authority.getName());
    }
}
