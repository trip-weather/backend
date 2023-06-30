package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Data;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
public class User {
    private String uuid;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String password;

    private boolean activated;

    private Set<Authority> authorities;

    private String resetKey;

    private String activationKey;

    private LocalDateTime createdDate;

    private LocalDateTime resetDate = null;
}