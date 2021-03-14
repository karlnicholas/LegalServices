package com.github.karlnicholas.legalservices.opinionview.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewCache;

import reactor.core.publisher.Mono;

@Component
public class OpinionViewControllerHandler {
	private final OpinionViewCache opinionViewCache;
	
	public OpinionViewControllerHandler(OpinionViewCache opinionViewCache) {
		this.opinionViewCache = opinionViewCache;

	}

	public Mono<ServerResponse> getOpinionViews(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewCache.getCache());
	}

	public Mono<ServerResponse> getOpinionViewDates(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewCache.getCache());
	}
}
