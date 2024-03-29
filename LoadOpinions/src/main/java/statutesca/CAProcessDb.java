package statutesca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Pattern;

import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.BeanProcessor;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.ResultSetHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;

import com.github.karlnicholas.legalservices.statute.StatuteRange;
import com.github.karlnicholas.legalservices.statute.StatutesBaseClass;
import com.github.karlnicholas.legalservices.statute.StatutesLeaf;
import com.github.karlnicholas.legalservices.statute.StatutesNode;
import com.github.karlnicholas.legalservices.statute.StatutesRoot;
import com.github.karlnicholas.legalservices.statute.api.IStatuteApi;
import com.github.karlnicholas.legalservices.statuteca.statuteapi.CAStatuteApiImpl;


public class CAProcessDb {

	protected final QueryRunner queryRunner;
	protected static final String url = "jdbc:mysql://localhost:3306/capublic?allowPublicKeyRetrieval=true&autoReconnect=true&useSSL=false";
//	protected static final String driver = "com.mysql.jdbc.Driver";
	protected static final String driver = "com.mysql.cj.jdbc.Driver";
	protected static final String usr = "root";
	protected static final String pwd = "Fr0m5n@w";
	protected final Connection conn;
	protected final ResultSetHandler<List<LawForCodeSections>> lawForCodeHandler;
	protected final ResultSetHandler<List<LawCode>> lawCodeHandler;
	private List<LawSection> lawSections;
	private static final String DIVISION = "Division";
	private static final int DIVISION_LEN = DIVISION.length();
	private static final String PART = "Part";
	private static final int PART_LEN = PART.length();
	private static final String TITLE = "Title";
	private static final int TITLE_LEN = TITLE.length();
	private static final String CHAPTER = "Chapter";
	private static final int CHAPTER_LEN = CHAPTER.length();
	private static final String ARTICLE = "Article";
	private static final int ARTICLE_LEN = ARTICLE.length();
	private final Pattern pattern = Pattern.compile("\\[(.*?)\\]$");
	protected int position;


	public CAProcessDb() throws SQLException {
		queryRunner = new QueryRunner();
		DbUtils.loadDriver(driver);
		conn = DriverManager.getConnection(url, usr, pwd);
		position = 1;
		lawCodeHandler = new BeanListHandler<LawCode>(LawCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
//		lawForCodeHandler = new BeanListHandler<LawForCode>(LawForCode.class, new BasicRowProcessor(new GenerousBeanProcessor()));
		lawForCodeHandler = rs-> {
			List<LawForCodeSections> beanList = new ArrayList<>();
			BeanProcessor bp = new GenerousBeanProcessor();
			LawForCodeSections previous = null;
			while ( rs.next() ) {
				LawForCode lawForCode = bp.populateBean(rs, new LawForCode());
				if ( !beanList.isEmpty() && previous.getNode_treepath() != null && previous.getNode_treepath().equals(lawForCode.getNode_treepath()) ) {
					previous.getSections().add(new LawSection(lawForCode.getSection_num(), lawForCode.getContent_xml()));
				} else {
					LawForCodeSections codeSections = new LawForCodeSections(lawForCode);
					beanList.add(codeSections);
					previous = codeSections;
				}
			}
			return beanList;
		};
	}

	public List<LawCode> retrieveLawCodes() throws SQLException  {
		return queryRunner.query(conn, "select * from codes_tbl", lawCodeHandler);
	}

	public  List<LawForCodeSections> retrieveLawForCode(String lawCode) throws SQLException  {
		return queryRunner.query(conn,
			"select l.division,l.title,l.part,l.chapter,l.article,l.heading,l.active_flg,l.node_level,l.node_position,l.node_treepath,l.contains_law_sections,s.section_num,s.content_xml from law_toc_tbl l left outer join law_section_tbl s on s.id in " + 
			"(select id from law_toc_sections_tbl where law_code = l.law_code and node_treepath = l.node_treepath order by section_order)" + 
			"where l.law_code = ? and l.active_flg = 'Y'" + 
			"order by l.node_sequence", lawForCodeHandler, lawCode);
	}

	
	public StatutesRoot parseLawCode(String lawCode, BiConsumer<LawForCodeSections, StatutesLeaf> leafConsumer) throws SQLException {
		List<LawForCodeSections> lawForCodes = retrieveLawForCode(lawCode);
		// purposely don't call loadStatutes because we are getting them from the raw
		// files
		IStatuteApi iStatutesApi = new CAStatuteApiImpl();
	
		Stack<StatutesBaseClass> secStack = new Stack<>();
		StatutesRoot statutesRoot = null;
		boolean foundCode = false;
		
		for (LawForCodeSections lawForCode: lawForCodes) {
			if ( lawForCode.getHeading().toLowerCase().contains("title of act")) {
				continue;
			}
			if ( ! foundCode ) {
				statutesRoot = new StatutesRoot(
						lawCode, 
						iStatutesApi.getTitle(lawCode), 
						iStatutesApi.getShortTitle(lawCode), 
						lawCode + "-0");
				secStack.push(statutesRoot);
				foundCode = true;
				continue;
			}
			if (secStack.size() > (lawForCode.getNode_level())) {
				while (secStack.size() > (lawForCode.getNode_level())) {
					secStack.pop();
				}
			}
			String heading = lawForCode.getHeading().toUpperCase();
			String part = "";
			String partNumber = ".";
			if (heading.substring(0, DIVISION_LEN).equalsIgnoreCase(DIVISION)) {
				part = DIVISION;
				partNumber = lawForCode.getDivision() == null ? "" : lawForCode.getDivision().toUpperCase();
				heading = heading.replace(DIVISION.toUpperCase() + " " + partNumber, "").trim();
			}
			if (heading.substring(0, PART_LEN).equalsIgnoreCase(PART)) {
				part = PART;
				partNumber = lawForCode.getPart() == null ? "" : lawForCode.getPart().toUpperCase();
				heading = heading.replace(PART.toUpperCase() + " " + partNumber, "").trim();
			}
			if (heading.substring(0, TITLE_LEN).equalsIgnoreCase(TITLE)) {
				part = TITLE;
				partNumber = lawForCode.getTitle() == null ? "" : lawForCode.getTitle().toUpperCase();
				heading = heading.replace(TITLE.toUpperCase() + " " + partNumber, "").trim();
			}
			if (heading.substring(0, CHAPTER_LEN).equalsIgnoreCase(CHAPTER)) {
				part = CHAPTER;
				partNumber = lawForCode.getChapter() == null ? "" : lawForCode.getChapter().toUpperCase();
				heading = heading.replace(CHAPTER.toUpperCase() + " " + partNumber, "").trim();
			}
			if (heading.substring(0, ARTICLE_LEN).equalsIgnoreCase(ARTICLE)) {
				part = ARTICLE;
				partNumber = lawForCode.getArticle() == null ? "" : lawForCode.getArticle().toUpperCase();
				heading = heading.replace(ARTICLE.toUpperCase() + " " + partNumber, "").trim();
			}
			if ( partNumber != null && partNumber.length() > 0 && partNumber.charAt(partNumber.length()-1) == '.')
				partNumber = partNumber.substring(0, partNumber.length()-1);
			StatutesBaseClass parent = secStack.peek();
			String fullFacet = parent.getFullFacet() + "/" + lawCode + "-" + lawForCode.getNode_level() + "-" + lawForCode.getNode_position();
			StatutesBaseClass statutesBaseClass;
			if (lawForCode.getContains_law_sections().equalsIgnoreCase("N")) {
				statutesBaseClass = new StatutesNode(
						parent, 
						fullFacet, 
						part, 
						partNumber, 
						heading, 
						lawForCode.getNode_level()
					);
				if (secStack.size() < (lawForCode.getNode_level() + 1)) {
					secStack.push(statutesBaseClass);
				}
			} else {
				StatutesLeaf statutesLeaf = new StatutesLeaf(
						parent, 
						fullFacet, 
						part,  
						partNumber, 
						heading, 
						lawForCode.getNode_level(), 
						new StatuteRange()
					);
				leafConsumer.accept(lawForCode, statutesLeaf);
				statutesBaseClass = statutesLeaf;
						
			}

			parent.addReference(statutesBaseClass);
			position++;
		}
		trimHeadings(statutesRoot);
		return statutesRoot;
	}

	private void trimHeadings(StatutesRoot statutesRoot) {
		Stack<StatutesBaseClass> stack = new Stack<>();
		stack.push(statutesRoot);
		// 
		while (!stack.isEmpty()) {
			// process this one then push its references
			StatutesBaseClass currentPos = stack.pop();
			String newHeader = prettyCase.apply( pattern.matcher(currentPos.getTitle()).replaceAll("") );
			currentPos.setTitle( newHeader );
			if ( currentPos.getReferences() != null  ) {
				for ( StatutesBaseClass currentRef: currentPos.getReferences() ) {
					stack.push(currentRef);
				}
			}
		}
	}

	private Function<String, String> prettyCase = s-> {
		String[] ss = s.split("\\s");
		StringBuilder sb = new StringBuilder();
		for ( String w: ss) {
			if ( w == null || w.isEmpty() ) {
				continue;
			}
			String wt = w.toLowerCase();
			char up = Character.toUpperCase( wt.charAt(0) );
			sb.append(up);
			if ( wt.length() > 1 ) {
				sb.append(wt.substring(1, wt.length()));
			}
			sb.append(' ');
		}
		return sb.toString().trim();
	};
	
	public List<LawSection> getLawSections() {
		return lawSections;
	}

}