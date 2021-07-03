package com.github.karlnicholas.legalservices.user.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * User class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
public class User {
    private Long id;
	private String username;
	private String password;
	private List<String> roles;
    private String firstName;
    private String lastName;
	private boolean enabled;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
