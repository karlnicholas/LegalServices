package com.github.karlnicholas.legalservices.user.security.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.github.karlnicholas.legalservices.user.model.RoleSave;

@Service
public class RoleDao {
	public List<RoleSave> listAvailable() {
		// TODO: implement stub method
		return null;
	}

	public RoleSave save(RoleSave roleSave) {
		// TODO Auto-generated method stub
		return null;
	}

	public Optional<Role> findByName(ERole roleUser) {
		// TODO Auto-generated method stub
		return Optional.empty();
	}
}
