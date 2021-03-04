package opinionsrestca;

import java.sql.SQLException;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import reactor.core.publisher.Mono;

@Component
public class OpinionsServiceHandler {
	private ParameterizedTypeReference<List<OpinionBase>> opinionBaseType;
	private ParameterizedTypeReference<List<OpinionKey>> opinionKeysType;
	private OpinionBaseDao opinionBaseDao;

	public OpinionsServiceHandler(OpinionBaseDao opinionBaseCrud) {
		this.opinionBaseDao = opinionBaseCrud;
		this.opinionKeysType = new ParameterizedTypeReference<List<OpinionKey>>() {};
		this.opinionBaseType = new ParameterizedTypeReference<List<OpinionBase>>() {};
	}

	public Mono<ServerResponse> getOpinionsWithStatuteCitations(ServerRequest request) {
		return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
				.body(request.bodyToMono(opinionKeysType).map(opinionKeys -> {
					try {
						return opinionBaseDao.getOpinionsWithStatuteCitations(opinionKeys);
					} catch (SQLException e) {
						throw new RuntimeException(e);
					}
				}), opinionBaseType);
	}

	public Mono<ServerResponse> getSlipOpinionList(ServerRequest request) {
		try {
			return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
					.body(BodyInserters.fromValue(opinionBaseDao.getSlipOpinionList()));
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public Mono<ServerResponse> updateSlipOpinionList(ServerRequest request) {
		return request.bodyToMono(String.class).flatMap(string->{
			try {
				opinionBaseDao.updateSlipOpinionList(string);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return ServerResponse.accepted().build();
		});
	}
}