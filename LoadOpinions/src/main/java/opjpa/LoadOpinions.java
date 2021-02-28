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
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/op", "op", "op");
		OpinionBaseCrud opinionBaseCrud = new OpinionBaseCrud();
		OpinionBaseOpinionCitationsCrud opinionBaseOpinionCitationsCrud = new OpinionBaseOpinionCitationsCrud();
		StatuteCitationCrud statuteCitationCrud = new StatuteCitationCrud();
		OpinionStatuteCitationCrud opinionStatuteCitationCrud = new OpinionStatuteCitationCrud();
		LoadHistoricalOpinions loadHistoricalOpinions = new LoadHistoricalOpinions(
			opinionBaseCrud, 
			opinionBaseOpinionCitationsCrud, 
			statuteCitationCrud, 
			opinionStatuteCitationCrud
		);
		con.setAutoCommit(false);
	    try {
	    	loadHistoricalOpinions.initializeDB(con);
	    }
	    catch(SQLException ex)
	    {
	        con.rollback();
	        con.setAutoCommit(true);
	        throw ex;
	    }
    	con.commit();
        con.setAutoCommit(true);
        System.out.println("loadHistoricalOpinions.initializeDB(): DONE");
	}

}
