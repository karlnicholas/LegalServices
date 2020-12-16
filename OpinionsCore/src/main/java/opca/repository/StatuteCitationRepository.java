package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public interface StatuteCitationRepository extends JpaRepository<StatuteCitation, Integer>{
		@Query(nativeQuery = true)
		List<StatuteCitation> findStatutesForKeys(@Param("keys") List<Integer> keys);

		@Query(nativeQuery = true)
		@EntityGraph("fetchGraphForStatutesWithReferringOpinions")
		List<StatuteCitation> statutesWithReferringOpinions(@Param("statuteKeys") List<StatuteKey> statuteKeys);
}
