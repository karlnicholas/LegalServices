package opca.ejb.util;

import statutes.service.ReactiveStatutesService;
import statutes.service.client.ReactiveStatutesServiceClientImpl;

public class ReactiveStatutesServiceFactory {
	private static String rsLocation;
	private static final String defaultAddress = "http://localhost:8080/statutesrs/rs/";
	static {
		rsLocation = System.getenv("statutesrsservice");
		if (rsLocation == null)
			rsLocation = defaultAddress;
	}
	public static ReactiveStatutesService getReactiveStatutesServiceClient() {
			return new ReactiveStatutesServiceClientImpl(rsLocation);
	}
}
