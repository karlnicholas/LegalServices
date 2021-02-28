package opinions.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import opca.model.OpinionBase;
import opca.model.OpinionKey;

public interface OpinionsService {
	String OPINIONCITATIONS = "opinioncitations";
	ResponseEntity<List<OpinionBase>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
}
