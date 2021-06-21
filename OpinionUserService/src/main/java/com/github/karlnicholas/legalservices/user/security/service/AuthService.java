package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.security.dao.UserDao;
import com.github.karlnicholas.legalservices.user.dto.AuthResultDto;
import com.github.karlnicholas.legalservices.user.dto.UserLoginDto;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.security.Key;
import java.util.*;

import javax.security.auth.login.FailedLoginException;

/**
 * SecurityService class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
@Service
public class AuthService {
	private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final Key secretKey;

    @Value("${jwt.expiration}")
    private String defaultExpirationTimeInSecondsConf;

    public AuthService(Key secretKey, UserDao userDao, PasswordEncoder passwordEncoder) {
    	this.secretKey = secretKey;
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
                .setSubject(applicationUser.getUsername())
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(secretKey)
                .compact();

        AuthResultDto authResultDto = new AuthResultDto();
        authResultDto.setToken(token);
        authResultDto.setUsername(applicationUser.getUsername());
        authResultDto.setIssuedAt(createdDate);
        authResultDto.setExpiresAt(expirationDate);
        return authResultDto;
    }

	public Mono<AuthResultDto> authenticate(Mono<UserLoginDto> ApplicationUserLoginMono) {
		return ApplicationUserLoginMono.flatMap(ApplicationUserLogin->{
			return Mono.justOrEmpty(userDao.findByUsername(ApplicationUserLogin.getUsername()))
			.flatMap(applicationUser->{
				if (!passwordEncoder.matches(ApplicationUserLogin.getPassword(), applicationUser.getPassword()))
					return Mono.error(new FailedLoginException("Failed Login!"));
				return Mono.just(generateAccessToken(applicationUser));
			});
		})
		.switchIfEmpty(Mono.error(new FailedLoginException("Failed Login!")));
	}
}
