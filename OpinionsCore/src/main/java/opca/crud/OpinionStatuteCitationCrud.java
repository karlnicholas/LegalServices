package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;

public class OpinionStatuteCitationCrud {
	private final Connection conn;
	public OpinionStatuteCitationCrud(Connection conn) {
		this.conn = conn;
	}

	public void insert(OpinionBase opinion) throws SQLException {
		if ( opinion.getStatuteCitations() != null ) {
			PreparedStatement ps = conn.prepareStatement("insert into opinionstatutecitation(countreferences, opinionbase_id, statutecitation_id) values(?,?,?)");
			for ( OpinionStatuteCitation osc: opinion.getStatuteCitations() ) {
				ps.setInt(1, osc.getCountReferences());
				ps.setInt(2, opinion.getId());
				ps.setInt(3, osc.getStatuteCitation().getId());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}
}
