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
import org.springframework.web.util.UriComponentsBuilder;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opinions.service.OpinionsService;

public class OpinionsServiceClientImpl implements OpinionsService {
	private final RestTemplate restTemplate;
	private final URI opinionCitationsURI;
	private final URI getSlipOpinionsURI;
	private final URI updateSlipOpinionsURI;

	public OpinionsServiceClientImpl(String baseUrl) {
		restTemplate = new RestTemplate();
		//set interceptors/requestFactory
		ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
		List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
		ris.add(ri);
		restTemplate.setInterceptors(ris);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		opinionCitationsURI = URI.create(baseUrl + OpinionsService.OPINIONCITATIONS);
		getSlipOpinionsURI = URI.create(baseUrl + OpinionsService.GETSLIPOPINIONS);
		updateSlipOpinionsURI = URI.create(baseUrl + OpinionsService.UPDATESLIPOPINIONS);
		
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
	public ResponseEntity<String> getSlipOpinionsList() {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.TEXT_PLAIN_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(getSlipOpinionsURI);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, String.class);	
	}

	@Override
	public void updateSlipOpinionsList(String string) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<String> requestEntity = new HttpEntity<>(string, requestHeaders);
		restTemplate.exchange(updateSlipOpinionsURI, HttpMethod.POST, requestEntity, Void.class);
	}

}
