package com.github.karlnicholas.legalservices.statute.service.reactive;

import com.github.karlnicholas.legalservices.statute.service.client.reactive.ReactiveStatuteServiceClientImpl;

public class ReactiveStatutesServiceFactory {
	private static String serviceUrl;
	private static final String defaultAddress = "http://localhost:8090/";
	static {
		serviceUrl = System.getenv("statutesrestca");
		if (serviceUrl == null)
			serviceUrl = defaultAddress;
	}
	public static ReactiveStatuteService getReactiveStatutesServiceClient() {
			return new ReactiveStatuteServiceClientImpl(serviceUrl);
	}
}
