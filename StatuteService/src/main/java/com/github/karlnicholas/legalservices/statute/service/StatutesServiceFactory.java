package com.github.karlnicholas.legalservices.statute.service;

import com.github.karlnicholas.legalservices.statute.service.client.StatuteServiceClientImpl;

public class StatutesServiceFactory {
	private static String rsLocation;
	private static final String defaultAddress = "http://localhost:8090/";
	static {
		rsLocation = System.getenv("statutesrsservice");
		if (rsLocation == null)
			rsLocation = defaultAddress;
	}
	public static StatuteService getStatutesServiceClient() {
			return new StatuteServiceClientImpl(rsLocation);
	}
}
