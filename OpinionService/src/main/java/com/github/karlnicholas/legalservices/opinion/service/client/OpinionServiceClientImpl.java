package com.github.karlnicholas.legalservices.opinion.service.client;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
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
	private final ParameterizedTypeReference<List<OpinionBase>> opinionListTypeReference;
	
	ObjectMapper objectMapper;
	
	public OpinionServiceClientImpl(String baseUrl, ObjectMapper objectMapper) {
		restTemplate = new RestTemplate();
		this.objectMapper = objectMapper;
		opinionListTypeReference =  new ParameterizedTypeReference<List<OpinionBase>>() {};
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
		return restTemplate.exchange(opinionCitationsUri, HttpMethod.POST, requestEntity, opinionListTypeReference);
	}

	@Override
	public ResponseEntity<String> callSlipOpinionUpdateNeeded() {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
		return restTemplate.getForEntity(slipOpinionUpdateNeededUri, String.class);
	}

	@Override
	public ResponseEntity<CaseListEntries> caseListEntries() {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		return restTemplate.getForEntity(caseEntriesUri, CaseListEntries.class);
	}

	@Override
	public ResponseEntity<Void> caseListEntryUpdates(List<CaseListEntry> currentCaseListEntries) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<List<CaseListEntry>> requestEntity = new HttpEntity<>(currentCaseListEntries, requestHeaders);
//try {
//	objectMapper.writerWithDefaultPrettyPrinter().writeValue(System.out, currentCaseListEntries);
//} catch (IOException e) {
//	// TODO Auto-generated catch block
//	e.printStackTrace();
//}
		return restTemplate.postForEntity(caseListEntryUpdatesUri, requestEntity, Void.class);
	}

	@Override
	public ResponseEntity<Void> caseListEntryUpdate(CaseListEntry caseListEntry) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<CaseListEntry> requestEntity = new HttpEntity<>(caseListEntry, requestHeaders);
		return restTemplate.postForEntity(caseListEntryUpdateUri, requestEntity, Void.class);
	}

}
