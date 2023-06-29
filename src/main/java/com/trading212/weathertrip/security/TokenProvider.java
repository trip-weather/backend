package com.trading212.weathertrip.security;

import com.trading212.weathertrip.config.GlobalProperties;
import com.trading212.weathertrip.repositories.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Component
public class TokenProvider {
    private final UserRepository userRepository;

    private final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private static final String AUTHORITIES_KEY = "auth";

    private static final String IS_AUTH_USER_PROFILE_COMPLETED = "is_profile_completed";

    private static final String INVALID_JWT_TOKEN = "Invalid JWT token.";

    private final Key key;

    private final JwtParser jwtParser;

    private final long tokenValidityInMilliseconds;

    private final long tokenValidityInMillisecondsForRememberMe;

    public TokenProvider(GlobalProperties globalProperties, UserRepository userRepository) {
        this.userRepository = userRepository;

        byte[] keyBytes;
        String secret = globalProperties.getSecurity().getAuthentication().getJwt().getBase64Secret();
        if (!ObjectUtils.isEmpty(secret)) {
            log.debug("Using a Base64-encoded JWT secret key");
            keyBytes = Decoders.BASE64.decode(secret);
        } else {
            log.warn(
                    "Warning: the JWT key used is not Base64-encoded. " +
                            "We recommend using the `jhipster.security.authentication.jwt.base64-secret` key for optimum security."
            );
            secret = globalProperties.getSecurity().getAuthentication().getJwt().getSecret();
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        key = Keys.hmacShaKeyFor(keyBytes);
        jwtParser = Jwts.parserBuilder().setSigningKey(key).build();
        this.tokenValidityInMilliseconds = 1000 * globalProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds();
        this.tokenValidityInMillisecondsForRememberMe =
                1000 * globalProperties.getSecurity().getAuthentication().getJwt().getTokenValidityInSecondsForRememberMe();
    }

    public String createToken(Authentication authentication, boolean rememberMe) {
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity;
        if (rememberMe) {
            validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
        } else {
            validity = new Date(now + this.tokenValidityInMilliseconds);
        }

        return Jwts
                .builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

//    public String createToken(com.trading212.weathertrip.domain.entities.User) {
//        String authorities = user.getAuthorities().stream().map(Authority::getName).collect(Collectors.joining(","));
//
//        Optional<com.koimoje.koimoje.domain.entities.User> authenticatedUser = this.userRepository.findOneByEmailIgnoreCase(user.getEmail());
//        boolean isAuthenticateUserProfileCompleted = authenticatedUser.isPresent() && authenticatedUser.get().getProfileStatus().equals(ProfileStatus.COMPLETED);
//
//        long now = (new Date()).getTime();
//        Date validity = new Date(now + this.tokenValidityInMillisecondsForRememberMe);
//
//        return Jwts
//                .builder()
//                .setSubject(user.getEmail())
//                .claim(AUTHORITIES_KEY, authorities)
//                .claim(IS_AUTH_USER_PROFILE_COMPLETED, isAuthenticateUserProfileCompleted)
//                .signWith(key, SignatureAlgorithm.HS512)
//                .setExpiration(validity)
//                .compact();
//    }

    public Authentication getAuthentication(String token) {
        Claims claims = jwtParser.parseClaimsJws(token).getBody();
        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        authorities.forEach(authority -> System.out.println(authority.getAuthority()));


        User principal = new User(claims.getSubject(), "", authorities);

        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }
//    public boolean isUserValid(String token) {
//        Claims claims = jwtParser.parseClaimsJws(token).getBody();
//
//        final String identity = claims.getSubject().toLowerCase();
//        UserStatus status = this.userRepository.getStatusByUsername(identity);
//
//        if(status == null) status = this.userRepository.getStatusByEmail(identity);
//        return status.equals(UserStatus.ACTIVE);
//    }

    public boolean validateToken(String authToken) {
        try {
            jwtParser.parseClaimsJws(authToken);

            return true;
        } catch (ExpiredJwtException e) {
//            this.securityMetersService.trackTokenExpired();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (UnsupportedJwtException e) {
//            this.securityMetersService.trackTokenUnsupported();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (MalformedJwtException e) {
//            this.securityMetersService.trackTokenMalformed();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (SignatureException e) {
//            this.securityMetersService.trackTokenInvalidSignature();

            log.trace(INVALID_JWT_TOKEN, e);
        } catch (IllegalArgumentException e) { // TODO: should we let it bubble (no catch), to avoid defensive programming and follow the fail-fast principle?
            log.error("Token validation error {}", e.getMessage());
        }

        return false;
    }
}
