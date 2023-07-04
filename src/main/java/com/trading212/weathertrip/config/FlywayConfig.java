package com.trading212.weathertrip.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.flyway.FlywayProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableConfigurationProperties(FlywayProperties.class)
public class FlywayConfig {
    private final DataSource dataSource;
    private final FlywayProperties flywayProperties;

    @Autowired
    public FlywayConfig(DataSource dataSource, FlywayProperties flywayProperties) {
        this.dataSource = dataSource;
        this.flywayProperties = flywayProperties;
    }
}
