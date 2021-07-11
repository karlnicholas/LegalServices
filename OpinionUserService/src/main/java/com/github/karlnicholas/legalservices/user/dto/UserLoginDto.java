package com.github.karlnicholas.legalservices.user.dto;

/**
 * UserLoginDto class
 *
 * @author Karl Nicholas
 */
public class UserLoginDto {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
