package com.github.karlnicholas.legalservices.user.security.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.User;
import com.github.karlnicholas.legalservices.user.model.UserSave;

@Service
public class UserDao {
	public UserSave findByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public Long countByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public List<UserSave> findUnverified() {
		// TODO: implement stub method
		return null;
		
	}
	public List<UserSave> findUnwelcomed() {
		// TODO: implement stub method
		return null;
		
	}
	public Long count() {
		// TODO Auto-generated method stub
		return null;
	}
	public UserSave save(UserSave userSave) {
		// TODO Auto-generated method stub
		return null;
	}
	public void saveAndFlush(UserSave userSave) {
		// TODO Auto-generated method stub
		
	}
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}
	public UserSave getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<UserSave> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	public Optional<User> findByUsername(String username) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}

}
