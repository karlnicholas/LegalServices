package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.util.Collections;

/**
 * UserService class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
@Service
public class ApplicationUserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserService(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ApplicationUser> createUser(ApplicationUser applicationUser) {
        applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        applicationUser.setRoles(Collections.singleton(new Role(ERole.ROLE_USER)));
        return Mono.just(userDao.save(applicationUser));
    }

    public Mono<ApplicationUser> getUser(String username) {
        return Mono.justOrEmpty(userDao.findByUsername(username));
    }
}
