package com.github.karlnicholas.legalservices.opinionview.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewData;

import reactor.core.publisher.Mono;

@Component
public class OpinionViewControllerHandler {
	private final OpinionViewData opinionViewData;
	
	public OpinionViewControllerHandler(OpinionViewData opinionViewData) {
		this.opinionViewData = opinionViewData;

	}

	public Mono<ServerResponse> getOpinionViews(ServerRequest request) {
System.out.println("opinionViewData: " + opinionViewData);
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewData.getOpinionViews());
	}

	public Mono<ServerResponse> getOpinionViewDates(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewData.getDateBrackets());
	}
}
