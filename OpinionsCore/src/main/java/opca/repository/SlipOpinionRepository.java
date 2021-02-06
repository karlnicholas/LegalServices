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

//	@Query("select s from SlipOpinions s left outer join fetch s.statuteCitations left outer join fetch s.opinionCitations left outer join fetch s.referringOpinions")
//	@EntityGraph(attributePaths = {"statuteCitations", "opinionCitations", "referringOpinions", })
//	List<SlipOpinion> loadOpinionsWithJoins();

//	@NamedEntityGraphs({ 
//		@NamedEntityGraph(name="fetchGraphForOpinionsWithJoins", attributeNodes= {
//			@NamedAttributeNode(value="statuteCitations", subgraph="fetchGraphForOpinionsWithJoinsPartB"), 
//		}, 
//		subgraphs= {
//			@NamedSubgraph(
//				name = "fetchGraphForOpinionsWithJoinsPartB", 
//				attributeNodes = { @NamedAttributeNode(value = "statuteCitation") } 
//			),
//		}), 	

	@Query(nativeQuery = true)
	@EntityGraph("fetchGraphForOpinionsWithJoinsForKeys")
	List<SlipOpinion> loadOpinionsWithJoinsForKeys(@Param("opinionKeys") List<OpinionKey> opinionKeys);

}
