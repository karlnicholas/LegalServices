package com.github.karlnicholas.legalservices.user.dto;

import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.github.karlnicholas.legalservices.user.model.Role;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * UserDto class
 *
 * @author Karl Nicholas
 */

public class ApplicationUserDto {
    private String email;
    private String firstName;
    private String lastName;
    private boolean verified;
    private boolean welcomed;
    private boolean optout;
    private LocalDate createDate;
    private Locale locale;
    private List<String> allTitles;
    private List<String> userTitles;
    private List<Role> roles;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isWelcomed() {
        return welcomed;
    }

    public void setWelcomed(boolean welcomed) {
        this.welcomed = welcomed;
    }

    public boolean isOptout() {
        return optout;
    }

    public void setOptout(boolean optout) {
        this.optout = optout;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate createDate) {
        this.createDate = createDate;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public List<String> getAllTitles() {
        return allTitles;
    }

    public void setAllTitles(List<String> allTitles) {
        this.allTitles = allTitles;
    }

    public List<String> getUserTitles() {
        return userTitles;
    }

    public void setUserTitles(List<String> userTitles) {
        this.userTitles = userTitles;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
