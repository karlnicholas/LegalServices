package statutes.service.server;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import statutes.SectionNumber;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.api.IStatutesApi;
import statutes.service.StatutesService;
import statutes.service.dto.StatuteKey;

@RestController
@RequestMapping("/")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class StatutesServiceServer implements StatutesService {
	private IStatutesApi iStatutesApi = ApiImplSingleton.getInstance().getStatutesApi();

	@Override
	@GetMapping(path = StatutesService.STATUTES, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesRoots() {
		log.info("get getStatutesRoots");
		return Mono.just(iStatutesApi.getStatutes())
				.map(ResponseEntity::ok);
	}

	@Override
	@GetMapping(path = StatutesService.STATUTESTITLES, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<StatutesTitles[]>> getStatutesTitles() {
		return Mono.just(ResponseEntity.ok(iStatutesApi.getStatutesTitles()));
	}

	@Override
	@GetMapping(path = StatutesService.STATUTEHIERARCHY, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<StatutesRoot>> getStatuteHierarchy(@RequestParam("fullFacet") String fullFacet) {
		return Mono.just(ResponseEntity.ok(iStatutesApi.getStatutesHierarchy(fullFacet)));
	}

	@Override
	@PostMapping(path=StatutesService.STATUTESANDHIERARCHIES, 
		consumes = MediaType.APPLICATION_JSON_VALUE, 
		produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<ResponseEntity<List<StatutesRoot>>> getStatutesAndHierarchies(List<StatuteKey> keys) {
		// Fill out the codeSections that these section are referencing ..
		// If possible ...
		
		return Flux.fromIterable(keys).map(key->{
			// This is a section
			String lawCode = key.getLawCode();
			SectionNumber sectionNumber = new SectionNumber();
			sectionNumber.setPosition(-1);
			sectionNumber.setSectionNumber(key.getSectionNumber());
			// int refCount = citation.getRefCount(opinionBase.getOpinionKey());
			// and place it within the sectionReference we previously parsed out of the
			// opinion
//					StatutesBaseClass statutesBaseClass = iStatutesApi.findReference(lawCode, sectionNumber);
			return iStatutesApi.findReference(lawCode, sectionNumber).getFullFacet();
		})
		.map(iStatutesApi::getStatutesHierarchy)
		.collectList()
		.map(ResponseEntity::ok);
	}

}
