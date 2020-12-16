package opca.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import opca.model.OpinionKey;
import opca.model.SlipOpinion;

public interface SlipOpinionRepository extends JpaRepository<SlipOpinion, Integer>{
	@Query(nativeQuery = true)
	@EntityGraph("fetchGraphForOpinionsWithJoins")
	List<SlipOpinion> loadOpinionsWithJoins();

	@Query(nativeQuery = true)
	@EntityGraph("fetchGraphForOpinionsWithJoins")
	List<SlipOpinion> loadOpinionsWithJoinsForKeys(@Param("opinionKeys") List<OpinionKey> opinionKeys);

}
