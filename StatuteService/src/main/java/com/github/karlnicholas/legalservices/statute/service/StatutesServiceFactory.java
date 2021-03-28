package com.github.karlnicholas.legalservices.statute.service;

import com.github.karlnicholas.legalservices.statute.service.client.StatuteServiceClientImpl;

public class StatutesServiceFactory {
	private static String serviceUrl;
	private static final String defaultAddress = "http://localhost:8090/";
	static {
		serviceUrl = System.getenv("statutesrestca");
		if (serviceUrl == null)
			serviceUrl = defaultAddress;
	}
	public static StatuteService getStatutesServiceClient() {
			return new StatuteServiceClientImpl(serviceUrl);
	}
}
