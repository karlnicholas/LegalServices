package com.github.karlnicholas.legalservices.user.model;

import org.springframework.security.core.GrantedAuthority;

/**
 * Role entity
 * @author karln
 *
 */
public class Role implements GrantedAuthority {
	private static final long serialVersionUID = 1L;
	private Integer id;
	private ERole eRole;
	public Role(ERole eRole) {
		this.eRole = eRole;
	}

	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public ERole geteRole() {
		return eRole;
	}
	public void seteRole(ERole eRole) {
		this.eRole = eRole;
	}

	@Override
	public String getAuthority() {
		// TODO Auto-generated method stub
		return eRole.name();
	}

}