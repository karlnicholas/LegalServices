package statutes.service.client;

import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.service.StatutesService;
import statutes.service.dto.KeyHierarchyPair;
import statutes.service.dto.StatuteHierarchy;
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
	public Flux<StatutesRoot> getStatutesRoots() {
		return webClient
				.get()
				.uri(StatutesService.STATUTES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(StatutesRoot.class);
	}
	
//	.body(BodyInserters.fromProducer(accounts, AccountDto.class))
//	.retrieve()
	@Override
	public Flux<StatutesTitles> getStatutesTitles() {
		return webClient
				.get()
				.uri(StatutesService.STATUTESTITLES)
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToFlux(StatutesTitles.class);
			
	}

	@Override
	public Mono<StatuteHierarchy> getStatuteHierarchy(String fullFacet) {
		return webClient
				.get()
				.uri(uriBuilder -> uriBuilder
				    .path(StatutesService.STATUTEHIERARCHY)
				    .queryParam("fullFacet", fullFacet)
				    .build())
				.accept(MediaType.APPLICATION_JSON)
				.retrieve()
				.bodyToMono(StatuteHierarchy.class);
	}

	@Override
	public Flux<KeyHierarchyPair> getStatutesAndHierarchies(Flux<StatuteKey> statuteKeys) {
		return webClient
				.post()
				.uri(StatutesService.STATUTESANDHIERARCHIES)
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromProducer(statuteKeys, StatuteKey.class))
				.retrieve()
				.bodyToFlux(KeyHierarchyPair.class);
	}

}
