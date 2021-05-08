package com.github.karlnicholas.legalservices.opinion.service.client;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.github.karlnicholas.legalservices.opinion.service.OpinionService;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;

public class OpinionServiceClientImpl implements OpinionService {
	private final RestTemplate restTemplate;
	private final URI opinionCitationsUri;
	private final URI slipOpinionUpdateNeededUri;
	private final URI caseEntriesUri;
	private final URI caseListEntryUpdatesUri;
	private final URI caseListEntryUpdateUri;
	
	public OpinionServiceClientImpl(String baseUrl) {
		restTemplate = new RestTemplate();
		//set interceptors/requestFactory
		if ( LoggingRequestInterceptor.log.isDebugEnabled() ) {
			ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
			List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
			ris.add(ri);
			restTemplate.setInterceptors(ris);
			restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		}
		opinionCitationsUri = URI.create(baseUrl + OpinionService.OPINIONCITATIONS);
		slipOpinionUpdateNeededUri = URI.create(baseUrl + OpinionService.SLIPOPINIONUPDATENEEDED);
		caseEntriesUri = URI.create(baseUrl + OpinionService.CASELISTENTRIES);
		caseListEntryUpdatesUri = URI.create(baseUrl + OpinionService.CASELISTENTRYUPDATES);
		caseListEntryUpdateUri = URI.create(baseUrl + OpinionService.CASELISTENTRYUPDATE);
	}
	
	@Override
	public ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<List<OpinionKey>> requestEntity = new HttpEntity<>(opinionKeys, requestHeaders);
		return restTemplate.exchange(opinionCitationsUri, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<OpinionBase>>() {});
	}

	@Override
	public ResponseEntity<String> callSlipOpinionUpdateNeeded() {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
		return restTemplate.getForEntity(slipOpinionUpdateNeededUri, String.class);
	}

	@Override
	public ResponseEntity<List<CaseListEntry>> caseListEntries() {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return restTemplate.exchange(caseEntriesUri, HttpMethod.GET, null, new ParameterizedTypeReference<List<CaseListEntry>>() {});
	}

	@Override
	public ResponseEntity<Void> caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<CaseListEntry>> requestEntity = new HttpEntity<>(currentCaseListEntries, requestHeaders);
		return restTemplate.exchange(caseListEntryUpdatesUri, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Void>() {});
	}

	@Override
	public ResponseEntity<Void> caseListEntryUpdate(CaseListEntry caseListEntry) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CaseListEntry> requestEntity = new HttpEntity<>(caseListEntry, requestHeaders);
		return restTemplate.exchange(caseListEntryUpdateUri, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<Void>() {});
	}

}
