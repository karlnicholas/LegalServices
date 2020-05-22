package statutes.service.server;

import org.springframework.http.MediaType;
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
import statutes.StatutesBaseClass;
import statutes.StatutesRoot;
import statutes.StatutesTitles;
import statutes.api.IStatutesApi;
import statutes.service.StatutesService;
import statutes.service.dto.KeyHierarchyPair;
import statutes.service.dto.StatuteHierarchy;
import statutes.service.dto.StatuteKey;

@RestController
@RequestMapping("/")
@Slf4j
@CrossOrigin(origins = "http://localhost:4200")
public class StatutesServiceServer implements StatutesService {
	private IStatutesApi iStatutesApi = ApiImplSingleton.getInstance().getStatutesApi();

	@Override
	@GetMapping(path = StatutesService.STATUTES, produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<StatutesRoot> getStatutesRoots() {
		log.info("get getStatutesRoots");
		return Flux.fromIterable(iStatutesApi.getStatutes());
	}

	@Override
	@GetMapping(path = StatutesService.STATUTESTITLES, produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<StatutesTitles> getStatutesTitles() {
		return Flux.fromArray(iStatutesApi.getStatutesTitles());
	}

	@Override
	@GetMapping(path = StatutesService.STATUTEHIERARCHY, produces = MediaType.APPLICATION_JSON_VALUE)
	public Mono<StatuteHierarchy> getStatuteHierarchy(@RequestParam("fullFacet") String fullFacet) {
		return Mono.just(iStatutesApi.getStatutesHierarchy(fullFacet));
	}

	@Override
	@PostMapping(path=StatutesService.STATUTESANDHIERARCHIES, 
		consumes = MediaType.APPLICATION_JSON_VALUE, 
		produces = MediaType.APPLICATION_JSON_VALUE)
	public Flux<KeyHierarchyPair> getStatutesAndHierarchies(Flux<StatuteKey> keys) {
		// Fill out the codeSections that these section are referencing ..
		// If possible ...
		return keys.map(key->{
			KeyHierarchyPair keyHierarchyPair = new KeyHierarchyPair();
			keyHierarchyPair.setStatuteKey(null);
			// This is a section
			String lawCode = key.getLawCode();
			SectionNumber sectionNumber = new SectionNumber();
			sectionNumber.setPosition(-1);
			sectionNumber.setSectionNumber(key.getSectionNumber());
			// int refCount = citation.getRefCount(opinionBase.getOpinionKey());
			// boolean designated = citation.getDesignated();
			if (lawCode != null) {
				// here we look for the Doc Section within the Code Section Hierachary
				// and place it within the sectionReference we previously parsed out of the
				// opinion
				StatutesBaseClass statutesBaseClass = iStatutesApi.findReference(lawCode, sectionNumber);
				if (statutesBaseClass != null) {
					StatuteHierarchy statuteHierarchy = iStatutesApi
							.getStatutesHierarchy(statutesBaseClass.getFullFacet());
					keyHierarchyPair.setStatuteKey(key);
					keyHierarchyPair.setStatutesPath(statuteHierarchy.getStatutesPath());
				}
			}
			return keyHierarchyPair;
		})
		.filter(keyHierarchyPair->keyHierarchyPair.getStatuteKey() != null);
	}

}
