package com.trading212.weathertrip.domain.entities;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAuthority {
    public UserAuthority() {
    }

    public UserAuthority(String user_uuid, String authority_name) {
        this.user_uuid = user_uuid;
        this.authority_name = authority_name;
    }

    private Long id;

    private String user_uuid;
    private String authority_name;
}
