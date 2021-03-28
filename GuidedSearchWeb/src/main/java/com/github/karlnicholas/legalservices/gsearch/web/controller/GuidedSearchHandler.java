package com.github.karlnicholas.legalservices.gsearch.web.controller;

import java.io.IOException;
//import java.util.logging.Logger;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.statute.service.reactive.ReactiveStatuteService;
import com.github.karlnicholas.legalservices.statute.service.reactive.ReactiveStatutesServiceFactory;
import com.github.karlnicholas.legalservices.gsearch.GSearch;
import com.github.karlnicholas.legalservices.gsearch.viewmodel.ViewModel;
import reactor.core.publisher.Mono;

@Component
public class GuidedSearchHandler {
	private final GSearch gsearch;
	public GuidedSearchHandler() throws IOException {
		ReactiveStatuteService reactiveStatutesService = ReactiveStatutesServiceFactory.getReactiveStatutesServiceClient();
		gsearch = new GSearch(reactiveStatutesService);
	}

	protected Mono<ServerResponse> getGSearch(ServerRequest serverRequest) {
		String path = serverRequest.queryParam("path").orElse(null);
		String term = serverRequest.queryParam("term").orElse(null);
		boolean frag = Boolean.parseBoolean(serverRequest.queryParam("frag").orElse("false"));
		try {
			return ServerResponse.ok().body(gsearch.handleRequest(path, term, frag), ViewModel.class);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}

