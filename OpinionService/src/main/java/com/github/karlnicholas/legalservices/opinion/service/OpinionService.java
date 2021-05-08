package com.github.karlnicholas.legalservices.opinion.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;

public interface OpinionService {
	String OPINIONCITATIONS = "opinioncitations";
	String SLIPOPINIONUPDATENEEDED = "slipopinionupdateneeded";
	String CASELISTENTRIES = "caseListEntries";
	String CASELISTENTRYUPDATES = "caseListEntryUpdates";
	String CASELISTENTRYUPDATE = "caseListEntryUpdate";
	ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
	ResponseEntity<String> callSlipOpinionUpdateNeeded();
	ResponseEntity<List<CaseListEntry>> caseListEntries();
	ResponseEntity<Void> caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries);
	ResponseEntity<Void> caseListEntryUpdate(CaseListEntry caseListEntry);
}
