package com.github.karlnicholas.legalservices.user.security.config;

import java.security.Key;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * WebSecurityConfig class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
@Configuration
public class KeySecurityConfig {
    @Bean
    public Key getSecretKey() {
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
