package com.github.karlnicholas.opinionservices.opinionview.controller;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class OpinionViewControllerRouter {
	@Bean
	public RouterFunction<ServerResponse> route(OpinionViewControllerHandler opinionViewControllerHandler) {
		return RouterFunctions
			.route(RequestPredicates.GET("/opinionviews/cases")
				.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionViewControllerHandler::getOpinionViews)
			.andRoute(RequestPredicates.GET("/opinionviews/dates")
					.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), opinionViewControllerHandler::getOpinionViewDates)
			;
	}

}
