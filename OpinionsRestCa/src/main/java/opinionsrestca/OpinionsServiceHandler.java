package opinionsrestca;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import opca.dao.OpinionBaseDao;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import reactor.core.publisher.Mono;

@Component
public class OpinionsServiceHandler {
	private ParameterizedTypeReference<List<OpinionBase>> opinionBaseType;
	private ParameterizedTypeReference<List<OpinionKey>> opinionKeysType;
	private OpinionBaseDao opinionBaseDao;

	public OpinionsServiceHandler(OpinionBaseDao opinionBaseDao) {
		this.opinionBaseDao = opinionBaseDao;
		this.opinionKeysType = new ParameterizedTypeReference<List<OpinionKey>>() {};
		this.opinionBaseType = new ParameterizedTypeReference<List<OpinionBase>>() {};
	}

	public Mono<ServerResponse> getOpinionsWithStatuteCitations(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(request.bodyToMono(opinionKeysType).map(opinionKeys -> {
					return opinionBaseDao.getOpinionsWithStatuteCitations(opinionKeys);
				}), opinionBaseType);
	}

	public Mono<ServerResponse> exampleOpinionKeys(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(request.bodyToMono(opinionKeysType).map(opinionKeys -> {
					return Collections.singletonList(new OpinionKey(3,4,1));
				}), opinionKeysType);
	}
}
