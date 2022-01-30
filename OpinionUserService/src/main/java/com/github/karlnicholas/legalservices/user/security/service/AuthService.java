package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.dao.UserDao;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.github.karlnicholas.legalservices.user.security.config.JwtProperties;
import com.github.karlnicholas.legalservices.user.security.payload.request.SigninRequest;
import com.github.karlnicholas.legalservices.user.security.payload.response.JwtResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;
import javax.security.auth.login.FailedLoginException;

import com.nimbusds.jose.*;
import com.nimbusds.jwt.*;

/**
 * SecurityService class
 *
 * @author Karl Nicholas
 */
@Service
public class AuthService {
	private final UserDao userDao;
	private final PasswordEncoder passwordEncoder;
	private final JwtProperties jwtProperties;

	public AuthService(JwtProperties jwtProperties, UserDao userDao, PasswordEncoder passwordEncoder) {
		this.jwtProperties = jwtProperties;
		this.userDao = userDao;
		this.passwordEncoder = passwordEncoder;
	}

	public JwtResponse generateAccessToken(ApplicationUser user) {
		long expirationTimeInMilliseconds = jwtProperties.getExpiration() * 1000;
		Date expirationDate = new Date(new Date().getTime() + expirationTimeInMilliseconds);
		Date createdDate = new Date();

		try {

			List<String> roles = user.getRoles() == null ? Collections.emptyList() : user.getRoles().stream().map(Role::getRole).collect(Collectors.toList());
			// Prepare JWT with claims set
			JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
					.claim("role", roles)
					.subject(user.getEmail())
					.issueTime(createdDate)
					.expirationTime(expirationDate)
					.build();

			SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

			// Apply the HMAC protection
			signedJWT.sign(jwtProperties.getJWSSigner());

			// Serialize to compact form, produces something like
			// eyJhbGciOiJIUzI1NiJ9.SGVsbG8sIHdvcmxkIQ.onO9Ihudz3WkiauDO2Uhyuz0Y18UASXlSc1eS0NkWyA
			String token = signedJWT.serialize();

			return JwtResponse.builder()
					.token(token)
					.username(user.getEmail())
					.build();
		} catch (JOSEException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getCause());
		}
	}
	
	public Mono<JwtResponse> authenticate(Mono<SigninRequest> signinRequestMono) {
		return signinRequestMono.flatMap(signingRequest -> Mono.justOrEmpty(userDao.findByEmail(signingRequest.getUsername())
				.filter(user -> passwordEncoder.matches(signingRequest.getPassword(), user.getPassword()))
				.map(this::generateAccessToken))).switchIfEmpty(Mono.error(new FailedLoginException("Failed Login!")));
	}

}
