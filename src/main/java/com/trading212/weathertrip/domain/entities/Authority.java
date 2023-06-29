package com.trading212.weathertrip.domain.entities;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class Authority implements Serializable {
    private Long id;

    private String name;

    public Authority(Long id, String name) {
        this.id = id;
        this.name = name;
    }
}
