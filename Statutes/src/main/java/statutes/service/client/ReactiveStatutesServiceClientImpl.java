package statutes.service.client;

import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ReactiveHttpOutputMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.ReactiveStatutesService;
import statutes.service.dto.StatuteKey;

public class ReactiveStatutesServiceClientImpl implements ReactiveStatutesService {
	private WebClient webClient;

	public ReactiveStatutesServiceClientImpl(String baseUrl) {
		webClient = WebClient.create(baseUrl);
	}
	
	@Override
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesRoots() {
		return webClient
				.get()
				.uri(ReactiveStatutesService.STATUTES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntityList(StatutesRoot.class);
	}
	
	@Override
	public Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles() {
		return webClient
				.get()
				.uri(ReactiveStatutesService.STATUTESTITLES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(StatutesTitles[].class);
			
	}

	@Override
	public Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(String fullFacet) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
				    .path(ReactiveStatutesService.STATUTEHIERARCHY)
				    .queryParam("fullFacet", fullFacet)
				    .build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(StatutesRoot.class);
	}

	@Override
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys) {
		WebClient client3 = WebClient
				  .builder()
				    .baseUrl("http://localhost:8090")
				    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
				  .build();
		
		WebClient.RequestBodySpec uri1 = client3
				  .method(HttpMethod.GET)
				  .uri("/");
		
		BodyInserter<List<StatuteKey>, ReactiveHttpOutputMessage> inserter3 = BodyInserters.fromValue(statuteKeys);
		
		WebClient.ResponseSpec response1 = uri1
				  .body(inserter3)
				    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				    .accept(MediaType.APPLICATION_JSON)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();

		return response1.toEntityList(StatutesRoot.class);
		
	}

}
