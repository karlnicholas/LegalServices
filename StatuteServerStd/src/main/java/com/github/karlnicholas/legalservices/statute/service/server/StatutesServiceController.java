package com.github.karlnicholas.legalservices.statute.service.server;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.statute.*;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class StatutesServiceController {
	private final IStatuteApi iStatutesApi;
	@Autowired ObjectMapper objectMapper;

	public StatutesServiceController() {
		this.iStatutesApi = ApiImplSingleton.getInstance().getStatuteApi();
	}

    @GetMapping(path=StatuteService.STATUTES)
	public ResponseEntity<StatutesRoots> getStatutesRoots() {
		return ResponseEntity.ok().body(StatutesRoots.builder().statuteRoots(iStatutesApi.getStatutes()).build());
	}

    @GetMapping(path=StatuteService.STATUTESTITLES)
	public ResponseEntity<StatutesTitles[]> getStatutesTitles() {
		return ResponseEntity.ok().body(iStatutesApi.getStatutesTitles());
	}

    @GetMapping(path=StatuteService.STATUTEHIERARCHY)
	public ResponseEntity<StatutesRoot> getStatuteHierarchy(@RequestParam String fullFacet) {
		return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(
				iStatutesApi.getStatutesHierarchy(fullFacet));
	}

    @PostMapping(path=StatuteService.STATUTESANDHIERARCHIES, consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<StatutesRoots> getStatutesAndHierarchies(@RequestBody List<StatuteKey> statutesKeys) {
		// This is a section
		List<StatutesRoot> rv = statutesKeys.stream().map(statuteKey -> {
			String lawCode = statuteKey.getLawCode();
			SectionNumber sectionNumber = new SectionNumber();
			sectionNumber.setPosition(-1);
			sectionNumber.setSectionNumber(statuteKey.getSectionNumber());
			return Optional.ofNullable(iStatutesApi.findReference(lawCode, sectionNumber))
					.map(statutesBaseClass->statutesBaseClass.getFullFacet());
		})
		.filter(Optional::isPresent)
		.map(Optional::get)
		.map(iStatutesApi::getStatutesHierarchy)
		.collect(Collectors.groupingBy(StatutesRoot::getLawCode, Collectors.reducing((sr1, sr2)->{
				return (StatutesRoot)sr1.mergeReferenceStatute(sr2);
			})))
		.values().stream().map(Optional::get).collect(Collectors.toList());

		return ResponseEntity.ok().body(StatutesRoots.builder().statuteRoots(rv).build());
	}

}
