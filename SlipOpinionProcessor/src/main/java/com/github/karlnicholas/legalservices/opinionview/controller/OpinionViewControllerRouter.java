package com.github.karlnicholas.legalservices.opinionview.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OpinionViewControllerRouter {
	@Bean
	public RouterFunction<ServerResponse> route(OpinionViewControllerHandler opinionViewControllerHandler, 
			@Value("classpath:/static/index.html") final Resource indexHtml
		) {
		return RouterFunctions
			.route(RequestPredicates.GET("/api/opinionviews/cases/{startDate}")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionViewControllerHandler::getOpinionViews)
			.andRoute(RequestPredicates.GET("/api/opinionviews/cases")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionViewControllerHandler::getRecentOpinionViews)
			.andRoute(RequestPredicates.GET("/api/opinionviews/dates")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionViewControllerHandler::getOpinionViewDates)
			;
	}

}
