package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import opca.model.OpinionStatuteCitation;
import opca.model.OpinionStatuteCitationId;

public interface OpinionStatuteCitationRepository extends JpaRepository<OpinionStatuteCitation, OpinionStatuteCitationId>{
	@Query(nativeQuery=true)
	@Modifying
	void deleteOpinionStatuteCitations(@Param("opinionIds") List<Integer> opinionIds);

}
