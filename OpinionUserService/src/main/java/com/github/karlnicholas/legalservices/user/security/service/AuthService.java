package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.dao.UserDao;
import com.github.karlnicholas.legalservices.user.dto.AuthResultDto;
import com.github.karlnicholas.legalservices.user.dto.UserLoginDto;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.security.auth.login.FailedLoginException;
import java.security.PrivateKey;
import java.util.Date;
import java.util.HashMap;

/**
 * SecurityService class
 *
 * @author Karl Nicholas
 */
@Service
public class AuthService {
	private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final PrivateKey privateKey;

    @Value("${jwt.expiration}")
    private String defaultExpirationTimeInSecondsConf;

    public AuthService(PrivateKey privateKey, UserDao userDao, PasswordEncoder passwordEncoder) {
    	this.privateKey = privateKey;
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    private AuthResultDto generateAccessToken(ApplicationUser applicationUser) {
        var claims = new HashMap<String, Object>();
        claims.put("role", applicationUser.getRoles());
        var expirationTimeInMilliseconds = Long.parseLong(defaultExpirationTimeInSecondsConf) * 1000;
        var expirationDate = new Date(new Date().getTime() + expirationTimeInMilliseconds);
        var createdDate = new Date();
        var token = Jwts.builder()
                .setClaims(claims)
                .setSubject(applicationUser.getEmail())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(privateKey)
                .compact();

        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setToken(token);
        authResultDto.setEmail(applicationUser.getEmail());
        authResultDto.setIssuedAt(createdDate);
        authResultDto.setExpiresAt(expirationDate);
        return authResultDto;
    }

	public Mono<AuthResultDto> authenticate(Mono<UserLoginDto> applicationUserLoginMono) {
        return applicationUserLoginMono.flatMap(applicationUserLogin->{
            return Mono.justOrEmpty(userDao.findByEmail(applicationUserLogin.getEmail()))
                    .flatMap(applicationUser->{
                        if (!passwordEncoder.matches(applicationUserLogin.getPassword(), applicationUser.getPassword()))
                            return Mono.error(new FailedLoginException("Failed Login!"));
                        return Mono.just(generateAccessToken(applicationUser));
                    });
        })
        .switchIfEmpty(Mono.error(new FailedLoginException("Failed Login!")));
    }
}
