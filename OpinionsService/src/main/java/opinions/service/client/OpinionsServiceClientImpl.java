package opinions.service.client;

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

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opinions.service.OpinionsService;

public class OpinionsServiceClientImpl implements OpinionsService {
	private final RestTemplate restTemplate;
	private final URI opinionCitationsURI;
	private final URI slipOpinionUpdateNeededURI;
	private final URI updateSlipOpinionListURI;
	
	public OpinionsServiceClientImpl(String baseUrl) {
		restTemplate = new RestTemplate();
		//set interceptors/requestFactory
		ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
		List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
		ris.add(ri);
		restTemplate.setInterceptors(ris);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		opinionCitationsURI = URI.create(baseUrl + OpinionsService.OPINIONCITATIONS);
		slipOpinionUpdateNeededURI = URI.create(baseUrl + OpinionsService.SLIPOPINIONUPDATENEEDED);
		updateSlipOpinionListURI = URI.create(baseUrl + OpinionsService.UPDATESLIPOPINIONLIST);	
	}
	
	@Override
	public ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<List<OpinionKey>> requestEntity = new HttpEntity<>(opinionKeys, requestHeaders);
		return restTemplate.exchange(opinionCitationsURI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<OpinionBase>>() {});
	}

	@Override
	public ResponseEntity<String> callSlipOpinionUpdateNeeded() {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setAccept(Collections.singletonList(MediaType.TEXT_PLAIN));
		return restTemplate.getForEntity(slipOpinionUpdateNeededURI, String.class);
	}

	@Override
	public ResponseEntity<Void> updateSlipOpinionList(String string) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.TEXT_PLAIN);
		HttpEntity<String> requestEntity = new HttpEntity<>(string, requestHeaders);
		return restTemplate.exchange(updateSlipOpinionListURI, HttpMethod.POST, requestEntity, Void.class);
	}

}
