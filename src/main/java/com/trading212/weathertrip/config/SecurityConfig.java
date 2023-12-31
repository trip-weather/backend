package com.trading212.weathertrip.config;

import com.trading212.weathertrip.security.CustomAuthenticationFilter;
import com.trading212.weathertrip.security.JWTConfigurer;
import com.trading212.weathertrip.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GlobalProperties globalProperties;
    private final TokenProvider tokenProvider;
    private final CorsFilter corsFilter;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .addFilterBefore(corsFilter, CustomAuthenticationFilter.class)
//                .addFilter(new CustomAuthenticationFilter())
                .exceptionHandling()
                .and()
                .headers()
                .contentSecurityPolicy(globalProperties.getSecurity().getContentSecurityPolicy())
                .and()
                .frameOptions()
                .deny()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(STATELESS)
                .and()
                .authorizeRequests()
                .requestMatchers("/api/account/change-password").authenticated()
                .requestMatchers("/api/user/{uuid}/profile", "/api/user/{uuid}/update").authenticated()
                .requestMatchers("/api/hotel/{id}/like", "/api/hotel/{id}/unlike").authenticated()
                .requestMatchers("/api/hotel/create-verification-session").authenticated()
                .requestMatchers("/api/flight/create-verification-session").authenticated()
                .requestMatchers("/api/hotel-order/{uuid}", "/api/flight-order/{uuid}").authenticated()
                .requestMatchers("/api/user/profile/reservations/hotel", "api/user/profile/reservations/flight-tickets").authenticated()
                .requestMatchers("/api/user/reservations/hotel").authenticated()
                .anyRequest().permitAll()
                .and()
                .httpBasic()
                .and()
                .apply(securityConfigurerAdapter());

        return http.build();
    }

    private JWTConfigurer securityConfigurerAdapter() {
        return new JWTConfigurer(tokenProvider);
    }
}
