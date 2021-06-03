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
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import reactor.core.publisher.Mono;

public class ReactiveOpinionServiceClientImpl implements ReactiveOpinionService {
	private final WebClient jsonWebClient = WebClient
			  .builder()
			  .baseUrl("http://localhost:8090")
			  .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
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

}
