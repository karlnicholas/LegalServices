package opca.dao;

import java.util.List;

import org.springframework.stereotype.Service;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.StatuteKey;

@Service
public class OpinionBaseDao {

		public OpinionBase findOpinionByKeyFetchReferringOpinions(StatuteKey key) {
			// TODO Auto-generated method stub
			return null;
		}
		
		public List<OpinionBase> opinionsWithReferringOpinions(List<OpinionKey> opinionKeys) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<OpinionBase> fetchOpinionCitationsForOpinions(List<Integer> opinionIds) {
			// TODO Auto-generated method stub
			return null;
		}

		public List<OpinionBase> fetchCitedOpinionsWithReferringOpinions(List<Integer> opinionIds) {
			// TODO Auto-generated method stub
			return null;
		}

		public void save(OpinionBase opinion) {
			// TODO Auto-generated method stub
			
		}
}
