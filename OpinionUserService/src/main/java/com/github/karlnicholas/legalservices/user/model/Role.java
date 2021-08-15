package com.github.karlnicholas.legalservices.user.model;

import java.util.Objects;

/**
 * Role entity
 * @author karln
 *
 */
public class Role {
	private Long id;
	private String role;

	public Role() {}

	public Role(Long id, String role ) {
		this.id = id;
		this.role = ERole.valueOf(role).name();
	}

	public Role(String name) {
		this.role = ERole.valueOf(role).name();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRole() { return role;	}

	public void setRole(String role) { this.role = role; }

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		return role.equalsIgnoreCase(((Role) o).role);
	}

	@Override
	public int hashCode() {
		return Objects.hash(role);
	}
}