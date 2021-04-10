package com.github.karlnicholas.legalservices.statute.service.server;

import java.util.ServiceLoader;

import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;

public class ApiImplSingleton {
	private IStatuteApi iStatuteApi;
	private static final ApiImplSingleton INSTANCE = new ApiImplSingleton();
	private ApiImplSingleton() {
		ServiceLoader<IStatuteApi> parserLoader = ServiceLoader.load(IStatuteApi.class);
		iStatuteApi = parserLoader.iterator().next();
		if ( iStatuteApi == null ) {
			throw new RuntimeException("ParserInterface not found.");
		}
		iStatuteApi.loadStatutes();
	}

	public static ApiImplSingleton getInstance() {
		return INSTANCE;
	}
	
	public IStatuteApi getStatuteApi() {
		return iStatuteApi;
	}
}
