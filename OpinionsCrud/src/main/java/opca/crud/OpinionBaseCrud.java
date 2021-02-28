package opca.crud;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import opca.model.DTYPES;
import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

public class OpinionBaseCrud {

	private final Connection conn;

	public OpinionBaseCrud(Connection conn) {
		this.conn = conn;
	}

	/**
	 *  
	 * @param opinionKeys
	 * @return
	 * @throws SQLException 
	 */
	public List<OpinionBase> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) throws SQLException {
		StringBuilder sb = new StringBuilder("select ooc.id as ooc_id, " + 
				"ooc.countreferringopinions as ooc_countreferringopinions, " + 
				"ooc.opiniondate as ooc_opiniondate, " + 
				"ooc.volume as ooc_volume, " + 
				"ooc.vset as ooc_vset, " + 
				"ooc.page as ooc_page, " + 
				"ooc.title as ooc_title, " + 
				"osc.countreferences as osc_countreferences,  " + 
				"sc.designated as sc_designated, " + 
				"sc.lawcode as sc_lawcode, " + 
				"sc.sectionnumber  as sc_sectionnumber " + 
				"from opinionbase ooc " + 
				"left outer join opinionstatutecitation osc on ooc.id = osc.opinionbase_id  " + 
				"left outer join statutecitation sc on osc.statutecitation_id = sc.id " + 
				"where (ooc.volume, ooc.vset, ooc.page) in ");
		sb.append("(");
		for ( int i=0; i < opinionKeys.size(); ++i ) {
			sb.append("(?,?,?),");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		PreparedStatement ps = conn.prepareStatement(sb.toString());
		for ( int i=0; i < opinionKeys.size(); ++i ) {
			ps.setInt(i*3+1, opinionKeys.get(i).getVolume());
			ps.setInt(i*3+2, opinionKeys.get(i).getVset());
			ps.setInt(i*3+3, opinionKeys.get(i).getPage());
		}
		
		return new ResultSetIterable<OpinionBase>(ps.executeQuery(), rs -> mapOpinionsWithStatuteCitations(rs)).stream()
			.collect(Collectors.groupingBy(OpinionBase::getId, Collectors.reducing((ob1, ob2)->{
				ob1.getStatuteCitations().addAll(ob2.getStatuteCitations());
				return ob1;
			}))).values().stream().map(Optional::get).collect(Collectors.toList());

//		List<OpinionBase> obResults = new ArrayList<>();
//		while ( rs.next()) {
//			obResults.add(mapOpinionsWithStatuteCitations(rs, rs.getRow()));
//		}
//		return obResults.stream().collect(Collectors.groupingBy(OpinionBase::getId, Collectors.reducing((ob1, ob2)->{
//			ob1.getStatuteCitations().addAll(ob2.getStatuteCitations());
//			return ob1;
//		}))).values().stream().map(Optional::get).collect(Collectors.toList());
	}

	private OpinionBase mapOpinionsWithStatuteCitations(ResultSet resultSet) throws SQLException {
		OpinionBase opinionBase = new OpinionBase(
				DTYPES.OPINIONBASE, 
				resultSet.getInt("ooc_volume"), 
				resultSet.getInt("ooc_vset"), 
				resultSet.getInt("ooc_page"));
		opinionBase.setId(resultSet.getInt("ooc_id"));
		opinionBase.setCountReferringOpinions(resultSet.getInt("ooc_countreferringopinions"));
		if ( resultSet.getObject("ooc_opiniondate") != null ) opinionBase.setOpinionDate(((Date)resultSet.getObject("ooc_opiniondate")).toLocalDate());
		opinionBase.setTitle(resultSet.getString("ooc_title"));
		opinionBase.setStatuteCitations(new HashSet<>());
		if ( resultSet.getString("sc_lawcode") != null &&  resultSet.getString("sc_sectionnumber") != null ) {
			StatuteCitation sc = new StatuteCitation(new StatuteKey(resultSet.getString("sc_lawcode"), resultSet.getString("sc_sectionnumber")));
			sc.setDesignated(resultSet.getBoolean("sc_designated"));
			OpinionStatuteCitation osc = new OpinionStatuteCitation(sc, opinionBase, resultSet.getInt("osc_countreferences"));
			opinionBase.getStatuteCitations().add(osc);
		}
		return opinionBase;
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
            	opinion.setId(Integer.valueOf(resultSet.getInt(1)));
            }
        }
	}

	public void insertBatch(List<OpinionBase> opinionBatch) throws SQLException {
		PreparedStatement ps = conn.prepareStatement(
				"insert into opinionbase(dtype, countreferringopinions, opiniondate, page, volume, vset, title) " +
				"values(?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

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
