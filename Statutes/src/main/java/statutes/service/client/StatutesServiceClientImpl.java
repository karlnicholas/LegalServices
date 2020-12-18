package statutes.service.client;

import java.net.URI;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
		requestHeaders.setContentType(new MediaType("application","json"));
		requestHeaders.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		HttpEntity<List<StatuteKey>> requestEntity = new HttpEntity<>(statuteKeys, requestHeaders);

		// Add the Jackson and String message converters
//		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
//		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

		return restTemplate.exchange(statutesAndHierarchiesURI, HttpMethod.GET, requestEntity, new ParameterizedTypeReference<List<StatutesRoot>>() {});
	}

}
