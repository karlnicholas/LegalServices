package statutes.service.client;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.dto.StatuteKey;

public class StatutesServiceClientImpl implements StatutesService {
//	private WebClient statutes;
//	private WebClient statutesTitles;
//	private WebClient statuteHierarchy;
//	private WebClient findStatutes;
	private WebClient webClient;

	public StatutesServiceClientImpl(String baseUrl) {
		webClient = WebClient.create(baseUrl);
/*			
			statutes = WebClient.create().get().uri(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTES, 
					null, null));
			.create(apiLocation);
			javax.ws.rs.client.Client client = ClientBuilder.newClient();
			statutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTES, 
					null, null)
				);

			statutesTitles = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTESTITLES, 
					null, null)
				);

			statuteHierarchy = client
					.target(new URI(
							apiLocation.getProtocol(), 
							apiLocation.getUserInfo(), 
							apiLocation.getHost(), 
							apiLocation.getPort(), 
							apiLocation.getPath() + StatutesService.STATUTEHIERARCHY, 
							null, null)
						);
			findStatutes = client
				.target(new URI(
					apiLocation.getProtocol(), 
					apiLocation.getUserInfo(), 
					apiLocation.getHost(), 
					apiLocation.getPort(), 
					apiLocation.getPath() + StatutesService.STATUTESANDHIERARCHIES, 
					null, null)
				);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
*/
	}
	
	@Override
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesRoots() {
		return webClient
				.get()
				.uri(StatutesService.STATUTES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntityList(StatutesRoot.class);
	}
	
//	.body(BodyInserters.fromProducer(accounts, AccountDto.class))
//	.retrieve()
	@Override
	public Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles() {
		return webClient
				.get()
				.uri(StatutesService.STATUTESTITLES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(StatutesTitles[].class);
			
	}

	@Override
	public Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(String fullFacet) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
				    .path(StatutesService.STATUTEHIERARCHY)
				    .queryParam("fullFacet", fullFacet)
				    .build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.toEntity(StatutesRoot.class);
	}

	@Override
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesAndHierarchies(List<StatuteKey> statuteKeys) {
		return webClient
				.post()
				.uri(StatutesService.STATUTESANDHIERARCHIES)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.bodyValue(statuteKeys)
				.retrieve()
				.toEntityList(StatutesRoot.class);
	}

}
