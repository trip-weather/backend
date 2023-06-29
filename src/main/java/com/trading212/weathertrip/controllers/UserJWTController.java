package com.trading212.weathertrip.controllers;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.trading212.weathertrip.controllers.validation.LoginUserValidation;
import com.trading212.weathertrip.repositories.UserRepository;
import com.trading212.weathertrip.services.MailService;
import com.trading212.weathertrip.security.JWTFilter;
import com.trading212.weathertrip.security.TokenProvider;
import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserJWTController {
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    private final MailService mailService;

    @PostMapping("/authenticate")
    public ResponseEntity authorize(@Valid @RequestBody LoginUserValidation loginUser) throws MessagingException {
        System.out.println("inside authenticate");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                loginUser.getUsername(),
                loginUser.getPassword()
        );

        System.out.println(authenticationToken.getAuthorities().isEmpty() ? "empty" : "full");
        authenticationToken.getAuthorities().forEach(authority -> System.out.println(authority.getAuthority()));
        System.out.println(authenticationToken.getName());
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        if (authentication == null) return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, loginUser.isRememberMe());
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);

        return new ResponseEntity<>(new JWTToken(jwt), httpHeaders, HttpStatus.OK);
    }

    /**
     * Object to return as body in JWT Authentication.
     */
    public static class JWTToken {

        private String idToken;

        public JWTToken(String idToken) {
            this.idToken = idToken;
        }

        @JsonProperty("id_token")
        String getIdToken() {
            return idToken;
        }

        void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }
}
