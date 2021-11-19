package com.github.karlnicholas.legalservices.opinionview.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.opinionview.kafka.OpinionViewData;
import com.github.karlnicholas.legalservices.slipopinion.processor.CaseListScraperComponent;

import reactor.core.publisher.Mono;

@Component
public class OpinionViewControllerHandler {
	private final OpinionViewData opinionViewData;
	private final CaseListScraperComponent caseListScraperComponent;
	
	public OpinionViewControllerHandler(OpinionViewData opinionViewData, 
			CaseListScraperComponent caseListScraperComponent
	) {
		this.opinionViewData = opinionViewData;
		this.caseListScraperComponent = caseListScraperComponent;

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
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewData.getRecentOpinionViews());
	}
	public Mono<ServerResponse> getOpinionViewDates(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.bodyValue(opinionViewData.getDateBrackets());
	}

	public Mono<ServerResponse> handlePolling(ServerRequest request) {
		try {
			return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
					.bodyValue(caseListScraperComponent.reportCurrentTime());
		} catch (SQLException | IOException e) {
			return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR).bodyValue(e.getMessage());
		}
	}
}
