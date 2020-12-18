package opca.ejb.util;

import statutes.service.StatutesService;
import statutes.service.client.StatutesServiceClientImpl;

public class StatutesServiceFactory {
	private static String rsLocation;
	private static final String defaultAddress = "http://localhost:8090/";
	static {
		rsLocation = System.getenv("statutesrsservice");
		if (rsLocation == null)
			rsLocation = defaultAddress;
	}
	public static StatutesService getStatutesServiceClient() {
			return new StatutesServiceClientImpl(rsLocation);
	}
}
