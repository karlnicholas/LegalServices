package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import opca.model.OpinionBase;

public class OpinionBaseOpinionCitationsCrud {
	private final Connection conn;

	public OpinionBaseOpinionCitationsCrud(Connection conn) {
		this.conn = conn;
	}

	public void insert(OpinionBase opinion) throws SQLException {
		if ( opinion.getOpinionCitations() != null ) {
			PreparedStatement ps = conn.prepareStatement("insert into opinionbase_opinioncitations(referringopinions_id, opinioncitations_id) values(?, ?)");
			for ( OpinionBase opinionCitation: opinion.getOpinionCitations() ) {
				ps.setInt(1, opinionCitation.getId());
				ps.setInt(2, opinion.getId());
				ps.addBatch();
			}
			ps.executeBatch();
		}
	}

}
