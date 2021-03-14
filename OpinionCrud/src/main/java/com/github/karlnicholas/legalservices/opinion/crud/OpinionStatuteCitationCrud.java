package com.github.karlnicholas.legalservices.opinion.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.karlnicholas.legalservices.opinion.model.OpinionBase;
import com.github.karlnicholas.legalservices.opinion.model.OpinionStatuteCitation;

public class OpinionStatuteCitationCrud {
	public void insertBatch(OpinionBase opinion, Connection con) throws SQLException {
		if ( opinion.getStatuteCitations() != null ) {
			try ( PreparedStatement ps = con.prepareStatement("insert into opinionstatutecitation(countreferences, opinionbase_id, statutecitation_id) values(?,?,?)"); 
			) {
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
}
