package com.github.karlnicholas.legalservices.user.mailer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.karlnicholas.legalservices.opinionview.model.OpinionView;
import com.github.karlnicholas.legalservices.user.model.UserSave;

@SuppressWarnings("serial")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EmailInformation implements Serializable {
	private String firstName;
	private String lastName;
	private String email;
	private String verifyKey;
	private int verifyCount;
	private String opRoute;
	private String comment;
	private Locale locale;
	private List<OpinionView> opinionCases;
	private String titles;
	private Map<String, Long> memoryMap;
	public EmailInformation(UserSave userSave) {
		this();
		this.firstName = userSave.getFirstName();
		this.lastName = userSave.getLastName();
		this.email = userSave.getEmail();
		this.verifyKey = userSave.getVerifyKey();
		this.verifyCount = userSave.getVerifyCount();
		this.titles = Arrays.toString(userSave.getTitles());
	}
	public EmailInformation(String email, String comment, Locale locale) {
		this();
		this.email = email;
		this.comment = comment;
		this.locale = locale;
	}
	public EmailInformation(UserSave userSave, List<OpinionView> opinionCases) {
		this();
		this.email = userSave.getEmail();
		this.titles = userSave.getTitles() != null && userSave.getTitles().length > 0 ? Arrays.toString(userSave.getTitles()) : "[All]";
		this.opinionCases = opinionCases;
	}
	public EmailInformation() {
		String vHost = System.getenv("oproute");
		if ( vHost != null ) {
			this.opRoute = vHost;
		} else {
			this.opRoute = "http://localhost:8080";
			
		}
	}
	public EmailInformation(UserSave userSave, Map<String, Long> memoryMap) {
		this();
		this.titles = userSave.getTitles() != null && userSave.getTitles().length > 0 ? Arrays.toString(userSave.getTitles()) : "[All]";
		this.email = userSave.getEmail();
		this.memoryMap = memoryMap;
	}
	public String getEmail() {
		return email;
	}
	public String getVerifyKey() {
		return verifyKey;
	}
	public int getVerifyCount() {
		return verifyCount;
	}
	public void setVerifyCount(int verifyCount) {
		this.verifyCount = verifyCount;
	}
	public String getOpRoute() {
		return opRoute;
	}
	public void setOpRoute(String opRoute) {
		this.opRoute = opRoute;
	}
	public String getComment() {
		return comment;
	}
	public Locale getLocale() {
		return locale;
	}
	public List<OpinionView> getOpinionCases() {
		return opinionCases;
	}
	public String getFirstName() {
		return firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public Map<String, Long> getMemoryMap() {
		return memoryMap;
	}
	public String getTitles() {
		return titles;
	}
}