package com.github.karlnicholas.legalservices.opinion.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;

public interface OpinionsService {
	String OPINIONCITATIONS = "opinioncitations";
	String SLIPOPINIONUPDATENEEDED = "slipopinionupdateneeded";
	String UPDATESLIPOPINIONLIST = "updateslipopinionlist";
	ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
	ResponseEntity<String> callSlipOpinionUpdateNeeded();
	ResponseEntity<Void> updateSlipOpinionList(String string);
}
