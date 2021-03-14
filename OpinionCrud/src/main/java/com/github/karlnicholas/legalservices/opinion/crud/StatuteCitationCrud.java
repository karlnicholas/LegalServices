package com.github.karlnicholas.legalservices.opinion.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.github.karlnicholas.legalservices.opinion.model.StatuteCitation;

public class StatuteCitationCrud {

	public void insertBatch(List<StatuteCitation> statuteBatch, Connection con) throws SQLException {
		try ( 
			PreparedStatement ps = con.prepareStatement(
					"insert into statutecitation(designated, lawcode, sectionnumber) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		) {

			for ( StatuteCitation statute: statuteBatch) { 
				ps.setBoolean(1, statute.getDesignated());
				ps.setString(2, statute.getStatuteKey().getLawCode());
				ps.setObject(3, statute.getStatuteKey().getSectionNumber());
				ps.addBatch();
			}
			ps.executeBatch();
			try ( ResultSet keys = ps.getGeneratedKeys(); )
			{
				for ( int i=0; i < statuteBatch.size(); ++i) {
					keys.next();
					statuteBatch.get(i).setId(keys.getInt(1));
				}
			}
		}
	}
}
