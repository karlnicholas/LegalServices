package com.github.karlnicholas.legalservices.opinionrestca;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import reactor.core.publisher.Mono;

@Component
public class OpinionsServiceHandler {
	private ParameterizedTypeReference<List<OpinionBase>> opinionBaseType;
	private ParameterizedTypeReference<List<OpinionKey>> opinionKeysType;
	private final OpinionBaseDao opinionBaseDao;

	public OpinionsServiceHandler(DataSource dataSource) {
		this.opinionBaseDao = new OpinionBaseDao(dataSource);
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

}
