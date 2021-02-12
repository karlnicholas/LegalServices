package opca.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import opca.model.OpinionKey;
import opca.model.SlipOpinion;

@Service
public class SlipOpinionDao {
	public List<SlipOpinion> loadOpinionsWithJoins() {
		// TODO Auto-generated method stub
		return null;
	}
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

	public List<SlipOpinion> loadOpinionsWithJoinsForKeys(List<OpinionKey> opinionKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SlipOpinion> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(SlipOpinion slipOpinion) {
		// TODO Auto-generated method stub
		
	}

	public void delete(SlipOpinion deleteOpinion) {
		// TODO Auto-generated method stub
		
	}

}
