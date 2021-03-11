package opinions.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import reactor.core.publisher.Mono;

public interface ReactiveOpinionsService {
	String OPINIONCITATIONS = "opinioncitations";
	String SLIPOPINIONUPDATENEEDED = "slipopinionupdateneeded";
	String UPDATESLIPOPINIONLIST = "updateslipopinionlist";
	Mono<ResponseEntity<List<OpinionBase>>> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys);
	Mono<ResponseEntity<String>> callSlipOpinionUpdateNeeded();
	Mono<ResponseEntity<Void>> updateSlipOpinionList(String string);
}
