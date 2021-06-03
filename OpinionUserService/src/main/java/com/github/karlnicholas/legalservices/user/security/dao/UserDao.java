package com.github.karlnicholas.legalservices.user.security.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;

@Service
public class UserDao {
	public Optional<ApplicationUser> findByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public Long countByEmail(String email) {
		// TODO: implement stub method
		return null;
		
	}
	public List<ApplicationUser> findUnverified() {
		// TODO: implement stub method
		return null;
		
	}
	public List<ApplicationUser> findUnwelcomed() {
		// TODO: implement stub method
		return null;
		
	}
	public Long count() {
		// TODO Auto-generated method stub
		return null;
	}
	public ApplicationUser save(ApplicationUser user) {
		// TODO Auto-generated method stub
		return null;
	}
	public void saveAndFlush(ApplicationUser ApplicationUser) {
		// TODO Auto-generated method stub
		
	}
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		
	}
	public ApplicationUser getOne(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	public List<ApplicationUser> findAll() {
		// TODO Auto-generated method stub
		return null;
	}
	public Optional<ApplicationUser> findByUsername(String username) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
	public boolean existsByUsername(String username) {
		// TODO Auto-generated method stub
		return false;
	}
	public boolean existsByEmail(String email) {
		// TODO Auto-generated method stub
		return false;
	}

}
