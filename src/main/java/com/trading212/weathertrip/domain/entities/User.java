package com.trading212.weathertrip.domain.entities;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class User{
    private String uuid;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String password;

    private boolean activated;

    private Set<Authority> authorities;
}