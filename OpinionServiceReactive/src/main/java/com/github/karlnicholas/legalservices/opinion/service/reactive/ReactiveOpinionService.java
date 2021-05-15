package com.github.karlnicholas.legalservices.opinion.service.reactive;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import reactor.core.publisher.Mono;

public interface ReactiveOpinionService {
	Mono<ResponseEntity<List<OpinionBase>>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
	Mono<ResponseEntity<String>> callSlipOpinionUpdateNeeded();
	Mono<ResponseEntity<CaseListEntries>> caseListEntries();
	Mono<ResponseEntity<Void>> caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries);
	Mono<ResponseEntity<Void>> caseListEntryUpdate(CaseListEntry caseListEntry);
}
