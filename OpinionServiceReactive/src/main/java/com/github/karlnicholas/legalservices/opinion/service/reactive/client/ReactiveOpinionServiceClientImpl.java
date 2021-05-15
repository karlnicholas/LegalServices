package com.github.karlnicholas.legalservices.opinion.service.reactive.client;

import java.nio.charset.Charset;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.opinion.service.reactive.ReactiveOpinionService;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import reactor.core.publisher.Mono;

public class ReactiveOpinionServiceClientImpl implements ReactiveOpinionService {
	private final WebClient jsonWebClient = WebClient
			  .builder()
			  .baseUrl("http://localhost:8090")
			  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
			  .build();
	
	private final WebClient textWebClient = WebClient
			  .builder()
			  .baseUrl("http://localhost:8090")
			  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE) 
			  .build();

	@Override
	public Mono<ResponseEntity<List<OpinionBase>>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) {
		return jsonWebClient.method(HttpMethod.POST)
			.uri(OpinionService.OPINIONCITATIONS)
			.body(BodyInserters.fromValue(opinionKeys))
			.accept(MediaType.APPLICATION_JSON)
			.acceptCharset(Charset.forName("UTF-8"))
			.retrieve().toEntityList(OpinionBase.class);
	}

	@Override
	public Mono<ResponseEntity<String>> callSlipOpinionUpdateNeeded() {
		return textWebClient
		  .method(HttpMethod.GET)
		  .uri("/")
		  .accept(MediaType.TEXT_PLAIN)
		  .acceptCharset(Charset.forName("UTF-8")).retrieve().toEntity(String.class);
		
	}

	@Override
	public Mono<ResponseEntity<CaseListEntries>> caseListEntries() {
		return jsonWebClient.method(HttpMethod.GET)
				.uri(OpinionService.CASELISTENTRIES)
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(Charset.forName("UTF-8"))
				.retrieve().toEntity(CaseListEntries.class);
		
	}

	@Override
	public Mono<ResponseEntity<Void>> caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries) {
		return jsonWebClient.method(HttpMethod.POST)
				.uri(OpinionService.CASELISTENTRYUPDATES)
				.body(BodyInserters.fromValue(currentCaseListEntries))
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(Charset.forName("UTF-8"))
				.retrieve().toBodilessEntity();
	}

	@Override
	public Mono<ResponseEntity<Void>> caseListEntryUpdate(CaseListEntry caseListEntry) {
		return jsonWebClient.method(HttpMethod.POST)
				.uri(OpinionService.CASELISTENTRYUPDATE)
				.body(BodyInserters.fromValue(caseListEntry))
				.accept(MediaType.APPLICATION_JSON)
				.acceptCharset(Charset.forName("UTF-8"))
				.retrieve().toBodilessEntity();
	}

}
