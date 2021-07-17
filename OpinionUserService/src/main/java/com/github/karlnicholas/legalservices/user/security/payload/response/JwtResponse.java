package com.github.karlnicholas.legalservices.user.security.payload.response;

import java.util.List;

public class JwtResponse {
	private String accessToken;
	private String type = "Bearer";
	private String username;
	private List<String> roles;

	public JwtResponse(String accessToken, String username, List<String> roles) {
		this.accessToken = accessToken;
		this.username = username;
		this.roles = roles;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public String getUsername() {
		return username;
	}

	public List<String> getRoles() {
		return roles;
	}
}
