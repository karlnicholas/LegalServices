package com.github.karlnicholas.legalservices.opinion.service.reactive;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;
import reactor.core.publisher.Mono;

public interface ReactiveOpinionService {
	Mono<ResponseEntity<List<OpinionBase>>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
}
