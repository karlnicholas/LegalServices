package com.github.karlnicholas.legalservices.opinionview.service;

import java.time.LocalDate;

public class ViewParameters {
	public int totalCaseCount;
	public int accountCaseCount;
	public String navbarText;
	public LocalDate sd;
	public LocalDate ed;
	public ViewParameters(LocalDate sd, LocalDate ed) {
		this.sd = sd;
		this.ed = ed;
	}
}

