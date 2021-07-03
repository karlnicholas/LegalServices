package com.github.karlnicholas.legalservices.opinion.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionKey;

public interface OpinionService {
	String OPINIONCITATIONS = "opinioncitations";
	ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
}
