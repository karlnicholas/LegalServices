package com.github.karlnicholas.legalservices.opinion.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;

public interface OpinionService {
	String OPINIONCITATIONS = "opinioncitations";
	String SLIPOPINIONUPDATENEEDED = "slipopinionupdateneeded";
	String UPDATESLIPOPINIONLIST = "updateslipopinionlist";
	ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
	ResponseEntity<String> callSlipOpinionUpdateNeeded();
	ResponseEntity<Void> updateSlipOpinionList(String string);
	List<CaseListEntry> caseListEntries();
	void caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries);
	void caseListEntryUpdate(CaseListEntry caseListEntry);
}
