package com.github.karlnicholas.legalservices.user.dto;

import java.util.Date;

/**
 * AuthResultDto class
 *
 * @author Karl Nicholas
 */
public class AuthResultDto {
    private String email;
    private String token;
    private Date issuedAt;
    private Date expiresAt;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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
