package opca.ejb.util;

import statutes.service.BlockingStatutesService;
import statutes.service.client.BlockingStatutesServiceClientImpl;

public class BlockingStatutesServiceFactory {
	private static String rsLocation;
	private static final String defaultAddress = "http://localhost:8090/";
	static {
		rsLocation = System.getenv("statutesrsservice");
		if (rsLocation == null)
			rsLocation = defaultAddress;
	}
	public static BlockingStatutesService getBlockingStatutesServiceClient() {
			return new BlockingStatutesServiceClientImpl(rsLocation);
	}
}
