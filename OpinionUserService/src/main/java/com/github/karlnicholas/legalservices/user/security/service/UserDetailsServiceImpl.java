package com.github.karlnicholas.legalservices.user.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;

import reactor.core.publisher.Mono;

//@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
	@Autowired
	UserDao userDao;

	@Override
	public Mono<UserDetails> findByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser user = userDao.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("ApplicationUser Not Found with username: " + username));

		return Mono.just(ApplicationUser.withUserDetails(user).build());
	}

}
