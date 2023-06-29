package com.trading212.weathertrip.config;

import com.trading212.weathertrip.domain.entities.Authority;
import com.trading212.weathertrip.domain.enums.UserRole;
import com.trading212.weathertrip.security.JWTConfigurer;
import com.trading212.weathertrip.security.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final GlobalProperties globalProperties;
    private final TokenProvider tokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
//                .addFilterBefore(corsFilter, CustomAuthenticationFilter.class)
////                .addFilter(new CustomAuthenticationFilter())
                .exceptionHandling()
//                .authenticationEntryPoint(problemSupport)
//                .accessDeniedHandler(problemSupport)
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
                .requestMatchers("/api/authenticate", "/api/register").permitAll()
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
