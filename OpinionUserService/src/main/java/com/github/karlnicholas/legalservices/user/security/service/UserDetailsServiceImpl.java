package com.github.karlnicholas.legalservices.user.security.service;

import com.github.karlnicholas.legalservices.user.dao.UserDao;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserDetailsServiceImpl implements ReactiveUserDetailsService {
	private final UserDao userDao;

	public UserDetailsServiceImpl(UserDao userDao) {
		this.userDao = userDao;
	}


	@Override
	public Mono<UserDetails> findByUsername(String username) {
		return Mono.justOrEmpty(
				userDao.findByEmail(username))
					.map(applicationUser -> User.withUsername(applicationUser.getEmail()).build()
				);
	}

}
