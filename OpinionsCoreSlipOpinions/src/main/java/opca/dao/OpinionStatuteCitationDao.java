package opca.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;

@Service
public class OpinionStatuteCitationDao {
	private final JdbcTemplate jdbcTemplate;
	public static final AtomicLong good = new AtomicLong();
	public static final AtomicLong bad = new AtomicLong();

	public OpinionStatuteCitationDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insert(OpinionBase opinion) {
		if ( opinion.getStatuteCitations() == null ) {
			bad.getAndIncrement();
			return;
		}
		good.getAndIncrement();
		final Iterator<OpinionStatuteCitation> obIt = opinion.getStatuteCitations().iterator();
		jdbcTemplate.batchUpdate("insert into opinionstatutecitation(countreferences, opinionbase_id, statutecitation_id) values(?,?,?)", 
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						OpinionStatuteCitation osc = obIt.next();
						ps.setInt(1, osc.getCountReferences());
						ps.setInt(2, opinion.getId());
						ps.setInt(3, osc.getStatuteCitation().getId());
					}
					@Override
					public int getBatchSize() {
						return opinion.getStatuteCitations().size();
					}
		    });

	}
}
