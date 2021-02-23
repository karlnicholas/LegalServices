package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import opca.model.OpinionBase;

public class OpinionBaseCrud {

	private final Connection conn;

	public OpinionBaseCrud(Connection conn) {
		this.conn = conn;
	}


	/**
	 * create table opinionbase 
	 * (dtype integer not null, 
	 * id integer not null auto_increment, 
	 * countreferringopinions integer not null, 
	 * opiniondate date, 
	 * page integer not null, 
	 * volume integer not null, 
	 * vset integer not null, 
	 * title varchar(127), 
	 * primary key (id)) engine=InnoDB;
	 * 
	 * @param opinion
	 * @throws SQLException 
	 */
	public void insert(OpinionBase opinion) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"insert into opinionbase(dtype, countreferringopinions, opiniondate, page, volume, vset, title) " +
				"values(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, opinion.getDtype().getDtype());
		ps.setInt(2, opinion.getCountReferringOpinions());
		ps.setObject(3, opinion.getOpinionDate());
		ps.setInt(4, opinion.getOpinionKey().getPage());
		ps.setInt(5, opinion.getOpinionKey().getVolume());
		ps.setInt(6, opinion.getOpinionKey().getVset());
		ps.setString(7, opinion.getTitle());
		ps.executeUpdate();
        try (ResultSet resultSet = ps.getGeneratedKeys()) {
            if (resultSet.first()) {
            	opinion.setId(resultSet.getInt(1));
            }
        }
	}

}
