package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import opca.model.StatuteCitation;

public class StatuteCitationCrud {
	private final Connection conn;
	
	public StatuteCitationCrud(Connection conn) {
		this.conn = conn;
	}

	public void insert(StatuteCitation statute) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"insert into statutecitation(designated, lawcode, sectionnumber) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		ps.setBoolean(1, statute.getDesignated());
		ps.setString(2, statute.getStatuteKey().getLawCode());
		ps.setObject(3, statute.getStatuteKey().getSectionNumber());
		ps.executeUpdate();
        try (ResultSet resultSet = ps.getGeneratedKeys()) {
            if (resultSet.first()) {
        		statute.setId(Integer.valueOf(resultSet.getInt(1)));
            }
        }
	}

	public void insertBatch(List<StatuteCitation> statuteBatch) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"insert into statutecitation(designated, lawcode, sectionnumber) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		for ( StatuteCitation statute: statuteBatch) { 
			ps.setBoolean(1, statute.getDesignated());
			ps.setString(2, statute.getStatuteKey().getLawCode());
			ps.setObject(3, statute.getStatuteKey().getSectionNumber());
			ps.addBatch();
		}
		ps.executeBatch();
		ResultSet keys = ps.getGeneratedKeys();
		for ( int i=0; i < statuteBatch.size(); ++i) {
			keys.next();
			statuteBatch.get(i).setId(keys.getInt(1));
		}
	}
}
