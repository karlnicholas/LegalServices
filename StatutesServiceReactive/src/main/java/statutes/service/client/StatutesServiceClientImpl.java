package statutes.service.client;

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

import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.ReactiveStatutesService;
import statutes.service.dto.StatuteKey;

public class StatutesServiceClientImpl implements StatutesService {
	private RestTemplate restTemplate;
	private URI statutesURI;
	private URI statuesTitlesURI;
	private URI statuteHierarchyURI;
	private URI statutesAndHierarchiesURI;

	public StatutesServiceClientImpl(String baseUrl) {
		restTemplate = new RestTemplate();
		//set interceptors/requestFactory
		ClientHttpRequestInterceptor ri = new LoggingRequestInterceptor();
		List<ClientHttpRequestInterceptor> ris = new ArrayList<ClientHttpRequestInterceptor>();
		ris.add(ri);
		restTemplate.setInterceptors(ris);
		restTemplate.setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		statutesURI = URI.create(baseUrl + ReactiveStatutesService.STATUTES);
		statuesTitlesURI = URI.create(baseUrl + ReactiveStatutesService.STATUTESTITLES);
		statuteHierarchyURI = URI.create(baseUrl + ReactiveStatutesService.STATUTEHIERARCHY);
		statutesAndHierarchiesURI = URI.create(baseUrl + ReactiveStatutesService.STATUTESANDHIERARCHIES);
	}
	
	@Override
	public ResponseEntity<List<StatutesRoot>> getStatutesRoots() {
		return restTemplate.exchange(statutesURI, HttpMethod.GET, HttpEntity.EMPTY,  new ParameterizedTypeReference<List<StatutesRoot>>() {});
	}
	
	@Override
	public ResponseEntity<StatutesTitles[]> getStatutesTitles() {
		return restTemplate.exchange(statuesTitlesURI, HttpMethod.GET, HttpEntity.EMPTY,  StatutesTitles[].class);
	}

	@Override
	public ResponseEntity<StatutesRoot> getStatuteHierarchy(String fullFacet) {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		UriComponentsBuilder builder = UriComponentsBuilder.fromUri(statuteHierarchyURI).queryParam("fullFacet", fullFacet);
		HttpEntity<?> entity = new HttpEntity<>(headers);
		return restTemplate.exchange(builder.toUriString(), HttpMethod.GET, entity, StatutesRoot.class);	
	}


	@Override
	public ResponseEntity<List<StatutesRoot>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys) {
		// Set the Content-Type header
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		HttpEntity<List<StatuteKey>> requestEntity = new HttpEntity<>(statuteKeys, requestHeaders);

		return restTemplate.exchange(statutesAndHierarchiesURI, HttpMethod.POST, requestEntity, new ParameterizedTypeReference<List<StatutesRoot>>() {});
	}

}
