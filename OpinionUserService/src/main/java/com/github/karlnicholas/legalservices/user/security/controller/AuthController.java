package com.github.karlnicholas.legalservices.user.security.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.ERole;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.github.karlnicholas.legalservices.user.security.dao.RoleDao;
import com.github.karlnicholas.legalservices.user.security.dao.UserDao;
import com.github.karlnicholas.legalservices.user.security.jwt.JwtUtils;
import com.github.karlnicholas.legalservices.user.security.payload.request.LoginRequest;
import com.github.karlnicholas.legalservices.user.security.payload.request.SignupRequest;
import com.github.karlnicholas.legalservices.user.security.payload.response.JwtResponse;
import com.github.karlnicholas.legalservices.user.security.payload.response.MessageResponse;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
	@Autowired
	AuthenticationManager authenticationManager;

	@Autowired
	UserDao userDao;

	@Autowired
	RoleDao roleDao;

	@Autowired
	PasswordEncoder encoder;

	@Autowired
	JwtUtils jwtUtils;

	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = jwtUtils.generateJwtToken(authentication);
		
		ApplicationUser userDetails = (ApplicationUser) authentication.getPrincipal();		
		List<String> roles = userDetails.getAuthorities().stream()
				.map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok(new JwtResponse(jwt, userDetails.getEmail(), roles));
	}

	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
		if (userDao.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Username is already taken!"));
		}

		if (userDao.existsByEmail(signUpRequest.getEmail())) {
			return ResponseEntity
					.badRequest()
					.body(new MessageResponse("Error: Email is already in use!"));
		}

		Set<String> strRoles = signUpRequest.getRole();
		Set<GrantedAuthority> roles = new HashSet<>();

		if (strRoles == null) {
			Role userRole = roleDao.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
			roles.add(userRole);
		} else {
			strRoles.forEach(role -> {
				switch (role) {
				case "admin":
					Role adminRole = roleDao.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(adminRole);

					break;
				case "operator":
					Role modRole = roleDao.findByName(ERole.ROLE_OPERATOR)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(modRole);

					break;
				default:
					Role userRole = roleDao.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("Error: Role is not found."));
					roles.add(userRole);
				}
			});
		}

		// Create new user's account
		ApplicationUser user = new ApplicationUser(signUpRequest.getEmail(),
							 encoder.encode(signUpRequest.getPassword()), roles);

		userDao.save(user);
		return ResponseEntity.ok(new MessageResponse("ApplicationUser registered successfully!"));
	}
}
