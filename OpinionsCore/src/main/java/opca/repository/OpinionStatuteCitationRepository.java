package opca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import opca.model.OpinionStatuteCitation;
import opca.model.OpinionStatuteCitationId;

public interface OpinionStatuteCitationRepository extends JpaRepository<OpinionStatuteCitation, OpinionStatuteCitationId>{

}
