package com.github.karlnicholas.legalservices.user.model;

import java.util.Objects;

/**
 * Role entity
 * @author karln
 *
 */
public class Role {
	private Long id;
	private ERole role;

	public Role(Long id, String role ) {
		this.id = id;
		this.role = ERole.valueOf(role);
	}
	public Role(ERole eRole) {
		this.role = eRole;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ERole geteRole() {
		return role;
	}

	public void seteRole(ERole eRole) {
		this.role = eRole;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Role role1 = (Role) o;
		return role == role1.role;
	}

	@Override
	public int hashCode() {
		return Objects.hash(role);
	}
}