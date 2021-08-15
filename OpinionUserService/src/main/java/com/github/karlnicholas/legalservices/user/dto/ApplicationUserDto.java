package com.github.karlnicholas.legalservices.user.dto;

import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.github.karlnicholas.legalservices.user.model.ApplicationUser;
import com.github.karlnicholas.legalservices.user.model.Role;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * UserDto class
 *
 * @author Karl Nicholas
 */

public class ApplicationUserDto {
    private Long id;
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

    public static ApplicationUserDto fromApplicationUser(ApplicationUser applicationUser, StatutesRoots statutesRoots) {
        ApplicationUserDto applicationUserDto = new ApplicationUserDto();
        applicationUserDto.setId(applicationUser.getId());
        applicationUserDto.setEmail(applicationUser.getEmail());
        applicationUserDto.setFirstName(applicationUser.getFirstName());
        applicationUserDto.setLastName(applicationUser.getLastName());
        applicationUserDto.setCreateDate(applicationUser.getCreateDate());
        applicationUserDto.setAllTitles(statutesRoots.getStatuteRoots().stream().map(StatutesRoot::getShortTitle).collect(Collectors.toList()));
        applicationUserDto.setUserTitles(Arrays.stream(applicationUser.getTitles()).collect(Collectors.toList()));
        applicationUserDto.setOptout(applicationUser.isOptout());
        applicationUserDto.setVerified(applicationUser.isVerified());
        applicationUserDto.setWelcomed(applicationUser.isWelcomed());
        applicationUserDto.setRoles(new ArrayList<>(applicationUser.getRoles()));
        applicationUserDto.setLocale(applicationUser.getLocale());
        return applicationUserDto;
    }

    public static ApplicationUser toApplicationUser(ApplicationUserDto applicationUserDto) {
        Set<Role> roles = new HashSet<>(applicationUserDto.getRoles());
        ApplicationUser applicationUser = new ApplicationUser(applicationUserDto.getEmail(), null, applicationUserDto.getLocale(), roles);
        applicationUser.setId(applicationUserDto.getId());
        applicationUser.setFirstName(applicationUserDto.getFirstName());
        applicationUser.setLastName(applicationUserDto.getLastName());
        applicationUser.setCreateDate(applicationUserDto.getCreateDate());
        applicationUser.setTitles(applicationUserDto.getUserTitles().toArray(new String[0]));
        applicationUser.setOptout(applicationUserDto.isOptout());
        applicationUser.setVerified(applicationUserDto.isVerified());
        applicationUser.setWelcomed(applicationUserDto.isWelcomed());
        return applicationUser;
    }

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

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
