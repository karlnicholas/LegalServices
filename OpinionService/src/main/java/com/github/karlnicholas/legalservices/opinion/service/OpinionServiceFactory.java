package com.github.karlnicholas.legalservices.opinion.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.opinion.service.client.OpinionServiceClientImpl;

public class OpinionServiceFactory {
	private static String serviceUrl;
	private static final String defaultAddress = "http://localhost:8091/";
	static {
		serviceUrl = System.getenv("opinionrestca");
		if (serviceUrl == null)
			serviceUrl = defaultAddress;
	}
	public static OpinionService getOpinionServiceClient(ObjectMapper objectMapper) {
		return new OpinionServiceClientImpl(serviceUrl, objectMapper);
	}
}
