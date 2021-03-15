package com.github.karlnicholas.legalservices.opinionview.controller;

import java.time.LocalDate;

import org.springframework.http.HttpStatus;
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
		try {
			LocalDate startDate = request.queryParam("startDate").map(LocalDate::parse).orElseThrow(()->new IllegalArgumentException("startDate invalid or missing"));
			LocalDate endDate = request.queryParam("endDate").map(LocalDate::parse).orElseThrow(()->new IllegalArgumentException("endDate invalid or missing"));
//			String endDate = request.queryParam("endDate");
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.bodyValue(opinionViewData.getOpinionViews(startDate, endDate));
		} catch ( IllegalArgumentException e) {
			return ServerResponse.badRequest().bodyValue(e.getMessage());
		} catch ( Exception e) {
			return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
		}
	}
	public Mono<ServerResponse> getOpinionViewDates(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewData.getDateBrackets());
	}
}
