package com.github.karlnicholas.legalservices.statute.service.server;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.statute.*;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

@Component
public class StatutesServiceHandler {
	private final IStatuteApi iStatutesApi;
	private final ParameterizedTypeReference<List<StatuteKey>> statutesKeysType;
	@Autowired ObjectMapper objectMapper;

	public StatutesServiceHandler() {
		this.iStatutesApi = ApiImplSingleton.getInstance().getStatuteApi();
		this.statutesKeysType = new ParameterizedTypeReference<List<StatuteKey>>() {};
	}

	public Mono<ServerResponse> getStatutesRoots(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(StatutesRoots.builder().statuteRoots(iStatutesApi.getStatutes()).build()));
	}

	public Mono<ServerResponse> getStatutesTitles(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(iStatutesApi.getStatutesTitles()));
	}

	public Mono<ServerResponse> getStatuteHierarchy(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(
				BodyInserters.fromValue(iStatutesApi.getStatutesHierarchy(request.queryParam("fullFacet").get())));
	}

	public Mono<ServerResponse> getStatutesAndHierarchies(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(request.bodyToMono(statutesKeysType).map(statutesKeys -> {
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
					return StatutesRoots.builder().statuteRoots(rv).build();
				}), StatutesRoots.class);
	}

}
