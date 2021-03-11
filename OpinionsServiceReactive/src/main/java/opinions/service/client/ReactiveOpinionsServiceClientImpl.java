package opinions.service.client;

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

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opinions.service.ReactiveOpinionsService;
import reactor.core.publisher.Mono;

public class ReactiveOpinionsServiceClientImpl implements ReactiveOpinionsService {

	@Override
	public Mono<ResponseEntity<List<OpinionBase>>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) {
		WebClient client3 = WebClient
				  .builder()
				    .baseUrl("http://localhost:8090")
				    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
				  .build();
		
		WebClient.RequestBodySpec uri1 = client3
				  .method(HttpMethod.GET)
				  .uri("/");
		
		BodyInserter<List<OpinionKey>, ReactiveHttpOutputMessage> inserter3 = BodyInserters.fromValue(opinionKeys);
		
		WebClient.ResponseSpec response1 = uri1
				  .body(inserter3)
				    .accept(MediaType.APPLICATION_JSON)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();
		
		return response1.toEntityList(OpinionBase.class);
	}

	@Override
	public Mono<ResponseEntity<Void>> updateSlipOpinionList(String string) {
		WebClient client3 = WebClient
				  .builder()
				    .baseUrl("http://localhost:8090")
				    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE) 
				  .build();
		
		WebClient.RequestBodySpec uri1 = client3
				  .method(HttpMethod.GET)
				  .uri("/");
		
		BodyInserter<String, ReactiveHttpOutputMessage> inserter3 = BodyInserters.fromValue(string);
		
		WebClient.ResponseSpec response1 = uri1
				  .body(inserter3)
				    .accept(MediaType.APPLICATION_JSON)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();
		
		return response1.toEntity(Void.class);
	}

	@Override
	public Mono<ResponseEntity<String>> callSlipOpinionUpdateNeeded() {
		WebClient client3 = WebClient
				  .builder()
				    .baseUrl("http://localhost:8090")
				    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE) 
				  .build();
		
		WebClient.RequestBodySpec uri1 = client3
				  .method(HttpMethod.GET)
				  .uri("/");
		
		WebClient.ResponseSpec response1 = uri1
				    .accept(MediaType.TEXT_PLAIN)
				    .acceptCharset(Charset.forName("UTF-8"))
				    .ifNoneMatch("*")
				    .ifModifiedSince(ZonedDateTime.now())
				  .retrieve();
		
		return response1.toEntity(String.class);
	}

}
