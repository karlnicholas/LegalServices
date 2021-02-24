package opca.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import opca.model.OpinionBase;

@Service
public class OpinionBaseOpinionCitationsDao {
	private final JdbcTemplate jdbcTemplate;
	public static final AtomicLong good = new AtomicLong();
	public static final AtomicLong bad = new AtomicLong();

	public OpinionBaseOpinionCitationsDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void insert(OpinionBase opinion) {
		if ( opinion.getOpinionCitations() == null ) {
			bad.getAndIncrement();
			return;
		}
		good.getAndIncrement();
		final Iterator<OpinionBase> obIt = opinion.getOpinionCitations().iterator();
		jdbcTemplate.batchUpdate("insert into opinionbase_opinioncitations(referringopinions_id, opinioncitations_id) values(?, ?)", 
				new BatchPreparedStatementSetter() {
					@Override
					public void setValues(PreparedStatement ps, int i) throws SQLException {
						OpinionBase ob = obIt.next();
						ps.setInt(1, ob.getId());
						ps.setInt(2, opinion.getId());
					}
					@Override
					public int getBatchSize() {
						return opinion.getOpinionCitations().size();
					}
		    });

	}

}
