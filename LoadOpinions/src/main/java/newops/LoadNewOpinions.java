package newops;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.github.karlnicholas.legalservices.opinion.crud.OpinionBaseCrud;
import com.github.karlnicholas.legalservices.opinion.crud.OpinionBaseOpinionCitationsCrud;
import com.github.karlnicholas.legalservices.opinion.crud.OpinionStatuteCitationCrud;
import com.github.karlnicholas.legalservices.opinion.crud.StatuteCitationCrud;

import loadnew.LoadHistoricalOpinions;

public class LoadNewOpinions {

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
	
//	public void loadOpinions() throws Exception {
//		Pattern pattern = Pattern.compile("[\\(\\)]");
//		ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(Paths.get("c:/users/karln/downloads/justia/casesCal.2d.zip")));
//		ZipEntry zipEntry = zipInputStream.getNextEntry();
//		while ( zipEntry != null ) {
//			Document d = Jsoup.parse(new String(zipInputStream.readAllBytes()));
//			Element o = d.selectFirst("div#opinion");
//			System.out.print( " " + o.select("p").size());
////			List<String> paragraphs = o.select("p").stream().map(Element::text).collect(Collectors.toList());
////			System.out.print( " " + o.select("a.related-case").size());
////			System.out.println(d.getAllElements().size());
//			zipInputStream.closeEntry();
//			System.out.println( " " + Arrays.asList(pattern.split(zipEntry.getName())));
//			zipEntry = zipInputStream.getNextEntry();
//		}
//		
//	}

}
