package opca.dao;

import java.sql.PreparedStatement;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import opca.model.OpinionStatuteCitation;

@Service
public class OpinionStatuteCitationDao {
	private final JdbcTemplate jdbcTemplate;

	public OpinionStatuteCitationDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void deleteOpinionStatuteCitations(List<Integer> opinionIds) {
		jdbcTemplate.update((conn)->{
			StringBuilder sb = new StringBuilder( "delete from opinionstatutecitation where opinionbase_id = in ");
			sb.append("(");
			for ( int i=0; i < opinionIds.size(); ++i ) {
				sb.append("?,");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			for ( int i=0; i < opinionIds.size(); ++i ) {
				ps.setInt(i, opinionIds.get(i));
			}
			return ps;
		});
	}

	public void insert(OpinionStatuteCitation opinionStatuteCitation) {
		jdbcTemplate.update((conn)->{
			PreparedStatement ps = conn.prepareStatement(
					"insert into opinionstatutecitation(countreferences, opinionbase_id, statutecitation_id) " +
					"values(?, ?, ?)");
			ps.setInt(0, opinionStatuteCitation.getCountReferences());
			ps.setInt(1, opinionStatuteCitation.getOpinionBase().getId());
			ps.setObject(2, opinionStatuteCitation.getStatuteCitation().getId());
			return ps;
		});
	}

	public void update(OpinionStatuteCitation opinionStatuteCitation) {
		jdbcTemplate.update((conn)->{
			PreparedStatement ps = conn.prepareStatement(
					"update opinionstatutecitation set countreferences = ?");
			ps.setInt(0, opinionStatuteCitation.getCountReferences());
			return ps;
		});
	}
}
