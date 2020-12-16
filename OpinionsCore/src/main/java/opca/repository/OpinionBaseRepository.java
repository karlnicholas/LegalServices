package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.StatuteKey;

public interface OpinionBaseRepository extends JpaRepository<OpinionBase, Integer>{
		@Query(nativeQuery=true)
		OpinionBase findOpinionByKeyFetchReferringOpinions(@Param("key") StatuteKey key);
		
		@Query(nativeQuery=true)
		List<OpinionBase> opinionsWithReferringOpinions(@Param("opinionKeys") List<OpinionKey> opinionKeys);

		@Query(nativeQuery=true)
		@EntityGraph("fetchGraphForSlipOpinions")
		List<OpinionBase> fetchOpinionCitationsForOpinions(@Param("opinionIds") List<Integer> opinionIds);

		@Query(nativeQuery=true)
		List<OpinionBase> fetchCitedOpinionsWithReferringOpinions(@Param("opinionIds") List<Integer> opinionIds);
}
