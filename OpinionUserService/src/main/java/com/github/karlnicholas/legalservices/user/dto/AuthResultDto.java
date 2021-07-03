package com.github.karlnicholas.legalservices.user.dto;

import java.util.Date;

/**
 * AuthResultDto class
 *
 * @author Erik Amaru Ortiz
 * @author Karl Nicholas
 */
public class AuthResultDto {
    private String username;
    private String token;
    private Date issuedAt;
    private Date expiresAt;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getIssuedAt() {
        return issuedAt;
    }

    public void setIssuedAt(Date issuedAt) {
        this.issuedAt = issuedAt;
    }

    public Date getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Date expiresAt) {
        this.expiresAt = expiresAt;
    }
}
