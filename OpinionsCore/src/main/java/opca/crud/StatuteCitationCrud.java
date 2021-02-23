package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
        		statute.setId(resultSet.getInt(1));
            }
        }
	}
}
