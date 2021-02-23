package opjpa;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import load.LoadHistoricalOpinions;
import opca.crud.OpinionBaseCrud;
import opca.crud.OpinionBaseOpinionCitationsCrud;
import opca.crud.OpinionStatuteCitationCrud;
import opca.crud.StatuteCitationCrud;

public class LoadOpinions {

	public static void main(String[] args) throws Exception {
		Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/op", "op", "op");
		OpinionBaseCrud opinionBaseCrud = new OpinionBaseCrud(conn);
		OpinionBaseOpinionCitationsCrud opinionBaseOpinionCitationsCrud = new OpinionBaseOpinionCitationsCrud(conn);
		StatuteCitationCrud statuteCitationCrud = new StatuteCitationCrud(conn);
		OpinionStatuteCitationCrud opinionStatuteCitationCrud = new OpinionStatuteCitationCrud(conn);
		try {
			LoadHistoricalOpinions loadHistoricalOpinions = new LoadHistoricalOpinions(
				opinionBaseCrud, 
				opinionBaseOpinionCitationsCrud, 
				statuteCitationCrud, 
				opinionStatuteCitationCrud
			);
			conn.setAutoCommit(false);
	    	loadHistoricalOpinions.initializeDB();
	    	conn.commit();
			System.out.println("loadHistoricalOpinions.initializeDB(): DONE");
		} catch ( SQLException sqlException ) {
	    	conn.rollback();
			sqlException.printStackTrace();	
		}
	}

}
