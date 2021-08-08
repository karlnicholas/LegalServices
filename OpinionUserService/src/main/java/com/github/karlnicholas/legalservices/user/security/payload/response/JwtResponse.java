package com.github.karlnicholas.legalservices.user.security.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	private String token;
	private String type = "Bearer";
	private String username;

	public JwtResponse(String token, String username) {
		this.token = token;
		this.username = username;
	}
}
