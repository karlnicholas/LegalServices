package com.github.karlnicholas.legalservices.statute.service.client.reactive;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.karlnicholas.legalservices.statute.service.StatuteService;
import com.github.karlnicholas.legalservices.statute.service.reactive.ReactiveStatuteService;
import com.gitub.karlnicholas.legalservices.statute.service.dto.StatutesRoots;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

import reactor.core.publisher.Mono;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.StatutesTitles;

public class ReactiveStatuteServiceClientImpl implements ReactiveStatuteService {
	private WebClient webClient;

	public ReactiveStatuteServiceClientImpl(String baseUrl) {
		webClient = WebClient.create(baseUrl);
	}

	@Override
	public Mono<ResponseEntity<StatutesRoots>> getStatutesRoots() {
		return webClient.get().uri(ReactiveStatuteService.STATUTES).accept(MediaType.APPLICATION_JSON).retrieve()
				.toEntity(StatutesRoots.class);
	}

	@Override
	public Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles() {
		return webClient.get().uri(ReactiveStatuteService.STATUTESTITLES).accept(MediaType.APPLICATION_JSON).retrieve()
				.toEntity(StatutesTitles[].class);

	}

	@Override
	public Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(String fullFacet) {
		return webClient.get()
				.uri(uriBuilder -> uriBuilder.path(ReactiveStatuteService.STATUTEHIERARCHY)
						.queryParam("fullFacet", fullFacet).build())
				.accept(MediaType.APPLICATION_JSON).retrieve().toEntity(StatutesRoot.class);
	}

	@Override
	public Mono<ResponseEntity<StatutesRoots>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys) {
		return webClient
			.post()
			.uri(StatuteService.STATUTESANDHIERARCHIES)
			.body(BodyInserters.fromValue(statuteKeys))
			.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(Charset.forName("UTF-8"))
			.ifNoneMatch("*")
			.ifModifiedSince(ZonedDateTime.now())
			.retrieve()
			.toEntity(StatutesRoots.class);
	}

}
