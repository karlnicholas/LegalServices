package com.github.karlnicholas.legalservices.user.security.jwt;

import java.security.Key;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

	@Value("${karlnicholas.app.jwtExpirationMs:86400000}")
	private int jwtExpirationMs;

	public String generateJwtToken(Authentication authentication) {

		ApplicationUser userPrincipal = (ApplicationUser) authentication.getPrincipal();

		return Jwts.builder()
				.setSubject((userPrincipal.getUsername()))
				.setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
				.signWith(key)
				.compact();
	}

	public String getUserNameFromJwtToken(String token) {
	     return Jwts.parserBuilder().setSigningKey(key).build()
	    		 .parseClaimsJws(token).getBody().getSubject();
	}

	public boolean validateJwtToken(String authToken) {
		try {
			Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
		     Jwts.parserBuilder().setSigningKey(key).build()
		    		 .parseClaimsJws(authToken);
			return true;
		} catch (MalformedJwtException e) {
			logger.error("Invalid JWT token: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.error("JWT token is expired: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("JWT token is unsupported: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}

		return false;
	}
}
