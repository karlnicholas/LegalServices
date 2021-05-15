package com.github.karlnicholas.legalservices.caselist.model;

import java.util.ArrayList;
import java.util.List;

public class CaseListEntries extends ArrayList<CaseListEntry> {
	private static final long serialVersionUID = 1L;
	public CaseListEntries() {
		super();
	}
	public CaseListEntries(List<CaseListEntry> caseListEntries) {
		super(caseListEntries);
	}
}
