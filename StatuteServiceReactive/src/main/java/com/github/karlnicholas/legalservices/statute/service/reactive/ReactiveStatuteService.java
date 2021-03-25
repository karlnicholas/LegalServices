package com.github.karlnicholas.legalservices.statute.service.reactive;

import java.util.List;

import org.springframework.http.ResponseEntity;

import reactor.core.publisher.Mono;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

public interface ReactiveStatuteService {

	String STATUTES = "statutes";
	String STATUTESTITLES = "statutestitles";
	String STATUTEHIERARCHY = "statutehierarchy";
	String STATUTESANDHIERARCHIES = "statutesandhierarchies";

	Mono<ResponseEntity<StatutesRoots>> getStatutesRoots();

	Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles();

	Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(String fullFacet);

	Mono<ResponseEntity<StatutesRoots>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys);

}
