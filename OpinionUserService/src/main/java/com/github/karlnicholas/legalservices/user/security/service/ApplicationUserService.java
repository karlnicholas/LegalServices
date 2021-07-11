package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.dao.UserDao;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * UserService class
 *
 * @author 
 * @author Karl Nicholas
 */
@Service
public class ApplicationUserService {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    public ApplicationUserService( UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<ApplicationUser> createUser(Mono<ApplicationUser> applicationUserMono) {
        return applicationUserMono.map(applicationUser -> {
            applicationUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
            userDao.insert(applicationUser);
            return applicationUser;
        });
    }

    public Optional<ApplicationUser> getUser(String email) {
        return userDao.findByEmail(email);
    }
}
