package com.github.karlnicholas.legalservices.user.model;

import java.time.LocalDate;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/*
create table user (id bigint not null auto_increment
, createdate datetime
, email varchar(255)
, emailupdates bit not null
, firstname varchar(255)
, lastname varchar(255)
, locale varchar(255)
, optout bit not null
, optoutkey varchar(255)
, password varchar(255)
, startverify bit not null
, titles tinyblob
, updatedate datetime
, verified bit not null
, verifycount integer not null
, verifyerrors integer not null
, verifykey varchar(255)
, welcomeerrors integer not null
, welcomed bit not null
, primary key (id)) engine=InnoDB;

 */
public class ApplicationUser {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String email;

	private String password;

	private String firstName;
	private String lastName;

	// user preferences
	private boolean emailUpdates;
	private boolean verified;
	private boolean startVerify;
	private String verifyKey;
	private int verifyErrors;
	private int verifyCount;
	//
	private boolean welcomed;
	private int welcomeErrors;
	private String optoutKey;
	private boolean optout;
	//
	private LocalDate updateDate;
	private LocalDate createDate;
	private Locale locale;
	private String[] titles;

	private Set<Role> roles;

	public ApplicationUser(String email, String password, Locale locale, Set<Role> roles) {
		this.email = email;
		this.password = password;
		this.roles = roles;
		init();
		this.locale = locale;
	}

	private void init() {
		this.createDate = LocalDate.now();
		this.updateDate = LocalDate.now().minusDays(4);
		this.verified = false;
		this.verifyKey = UUID.randomUUID().toString();
		this.verifyErrors = 0;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	/**
     * Get User's Password
     * @return User's Password
     */
    public String getPassword() {
        return password;
    }
    /**
     * Set User's Password
     * @param password to set.
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * Get Users's First Name
     * 
     * @return First Name
     */
    public String getFirstName() {
        return firstName;
    }
    /**
     * Set User's First Name
     * 
     * @param firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    /**
     * Get Users's Last Name
     * 
     * @return Last Name
     */
    public String getLastName() {
        return lastName;
    }
    /**
     * Set User's Last Name
     * 
     * @param lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    /**
     * Get Roles associated with User
     * @return List of Roles
     */
    public Set<Role> getRoles() {
        return roles;
    }
    /**
     * Set Roles associated with User
     * @param roles List of roles to set.
     */
    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
    /**
     * Check to see if the User is has Admin role
     * 
     * @return true if admin
     */
    public boolean isAdmin() {
        for ( Role role: roles ) {
            if ( ERole.ADMIN == ERole.valueOf(role.getRole()) ) return true;
        }
        return false;
    }

	public boolean isEmailUpdates() {
		return emailUpdates;
	}
	public void setEmailUpdates(boolean emailUpdates) {
		this.emailUpdates = emailUpdates;
	}
	public boolean isVerified() {
		return verified;
	}
	public void setVerified(boolean verified) {
		this.verified = verified;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public void setVerifyKey(String verifyKey) {
		this.verifyKey = verifyKey;
	}
	public int getVerifyErrors() {
		return verifyErrors;
	}
	public void setVerifyErrors(int verifyErrors) {
		this.verifyErrors = verifyErrors;
	}
	public int getVerifyCount() {
		return verifyCount;
	}
	public void setVerifyCount(int verifyCount) {
		this.verifyCount = verifyCount;
	}
	public boolean isWelcomed() {
		return welcomed;
	}
	public void setWelcomed(boolean welcomed) {
		this.welcomed = welcomed;
	}
	public int getWelcomeErrors() {
		return welcomeErrors;
	}
	public void setWelcomeErrors(int welcomeErrors) {
		this.welcomeErrors = welcomeErrors;
	}
	public String getOptoutKey() {
		return optoutKey;
	}
	public void setOptoutKey(String optoutKey) {
		this.optoutKey = optoutKey;
	}
	public boolean isOptout() {
		return optout;
	}
	public void setOptout(boolean optout) {
		this.optout = optout;
	}
	public LocalDate getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(LocalDate updateDate) {
		this.updateDate = updateDate;
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
	public String[] getTitles() {
		return titles;
	}
	public void setTitles(String[] codes) {
		this.titles = codes;
	}
	public boolean isStartVerify() {
		return startVerify;
	}
	public void setStartVerify(boolean startVerify) {
		this.startVerify = startVerify;
	}
}
