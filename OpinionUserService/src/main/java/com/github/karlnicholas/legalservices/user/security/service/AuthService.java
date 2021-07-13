package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.dao.UserDao;
import com.github.karlnicholas.legalservices.user.dto.AuthResultDto;
import com.github.karlnicholas.legalservices.user.dto.UserLoginDto;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.security.auth.login.FailedLoginException;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * SecurityService class
 *
 * @author Karl Nicholas
 */
@Service
public class AuthService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final byte[] sharedSecret;

    @Value("${jwt.expiration}")
    private String defaultExpirationTimeInSecondsConf;

    public AuthService(byte[] sharedSecret, UserDao userDao, PasswordEncoder passwordEncoder) {
        this.sharedSecret = sharedSecret;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    private AuthResultDto generateAccessToken(ApplicationUser applicationUser) {
        var expirationTimeInMilliseconds = Long.parseLong(defaultExpirationTimeInSecondsConf) * 1000;
        var expirationDate = new Date(new Date().getTime() + expirationTimeInMilliseconds);
        var createdDate = new Date();
        try {
            // Create HMAC signer
            JWSSigner signer = new MACSigner(sharedSecret);
            // Prepare JWT with claims set
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .claim("roles", applicationUser.getRoles().stream().map(role->role.geteRole().name()).collect(Collectors.toList()))
                    .subject(applicationUser.getEmail())
                    .issueTime(createdDate)
                    .expirationTime(expirationDate)
                    .build();
            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);
            // Apply the HMAC protection
            signedJWT.sign(signer);
            // Serialize to compact form, produces something like
            // eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
            String token = signedJWT.serialize();

            AuthResultDto authResultDto = new AuthResultDto();
            authResultDto.setToken(token);
            authResultDto.setEmail(applicationUser.getEmail());
            authResultDto.setIssuedAt(createdDate);
            authResultDto.setExpiresAt(expirationDate);
            return authResultDto;
        } catch (JOSEException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getCause());
        }
    }

    public Mono<AuthResultDto> authenticate(Mono<UserLoginDto> applicationUserLoginMono) {
        return applicationUserLoginMono.flatMap(applicationUserLogin -> {
            return Mono.justOrEmpty(userDao.findByEmail(applicationUserLogin.getEmail()))
                    .flatMap(applicationUser -> {
                        if (!passwordEncoder.matches(applicationUserLogin.getPassword(), applicationUser.getPassword()))
                            return Mono.error(new FailedLoginException("Failed Login!"));
                        return Mono.just(generateAccessToken(applicationUser));
                    });
        })
        .switchIfEmpty(Mono.error(new FailedLoginException("Failed Login!")));
    }
}
