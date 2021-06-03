package com.github.karlnicholas.legalservices.user.security.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	UserDao userDao;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		ApplicationUser user = userDao.findByEmail(username)
				.orElseThrow(() -> new UsernameNotFoundException("ApplicationUser Not Found with username: " + username));

		return ApplicationUser.withUserDetails(user).build();
	}

}
