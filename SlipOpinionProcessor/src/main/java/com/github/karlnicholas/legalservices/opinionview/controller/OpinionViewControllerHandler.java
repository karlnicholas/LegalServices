package com.github.karlnicholas.legalservices.opinionview.controller;

import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.slipopinion.processor.OpinionViewData;
import com.github.karlnicholas.legalservices.slipopinion.processor.CaseListScraperComponent;

import reactor.core.publisher.Mono;

@Component
public class OpinionViewControllerHandler {
	private final OpinionViewData opinionViewData;
	private final CaseListScraperComponent caseListScraperComponent;
	
	public OpinionViewControllerHandler(OpinionViewData opinionViewData, 
			CaseListScraperComponent caseListScraperComponent
	) {
		this.caseListScraperComponent = caseListScraperComponent; 
		this.opinionViewData = opinionViewData;

	}

	public Mono<ServerResponse> getOpinionViews(ServerRequest request) {
		try {
			LocalDate startDate = LocalDate.parse(request.pathVariable("startDate"));
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.bodyValue(opinionViewData.getOpinionViews(startDate));
		} catch ( IllegalArgumentException e) {
			return ServerResponse.badRequest().bodyValue(e.getMessage());
		} catch ( Exception e) {
			return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
		}
	}
	public Mono<ServerResponse> getRecentOpinionViews(ServerRequest request) {
		try {
			return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
					.bodyValue(opinionViewData.getOpinionViews());
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
