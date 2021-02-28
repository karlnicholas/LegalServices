package opca.crud;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import opca.model.OpinionBase;

public class OpinionBaseCrud {
	public void insertBatch(List<OpinionBase> opinionBatch, Connection con) throws SQLException {
		try ( PreparedStatement ps = con.prepareStatement(
						"insert into opinionbase(dtype, countreferringopinions, opiniondate, page, volume, vset, title) " +
						"values(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		) {
	
			for ( OpinionBase opinion: opinionBatch) { 
				ps.setInt(1, opinion.getDtype().getDtype());
				ps.setInt(2, opinion.getCountReferringOpinions());
				ps.setObject(3, opinion.getOpinionDate());
				ps.setInt(4, opinion.getOpinionKey().getPage());
				ps.setInt(5, opinion.getOpinionKey().getVolume());
				ps.setInt(6, opinion.getOpinionKey().getVset());
				ps.setString(7, opinion.getTitle());
				ps.addBatch();
			}
			ps.executeBatch();
			ResultSet keys = ps.getGeneratedKeys();
			for ( int i=0; i < opinionBatch.size(); ++i ) {
				keys.next();
				opinionBatch.get(i).setId(keys.getInt(1));
			}
		}
	}

}
