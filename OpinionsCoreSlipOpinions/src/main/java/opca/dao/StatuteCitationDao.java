package opca.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import opca.model.DTYPES;
import opca.model.OpinionBase;
import opca.model.OpinionStatuteCitation;
import opca.model.StatuteCitation;
import opca.model.StatuteKey;

@Service
public class StatuteCitationDao {
	private final JdbcTemplate jdbcTemplate;

	public StatuteCitationDao(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public List<StatuteCitation> findStatutesForKeys(List<Integer> keys) {
		return jdbcTemplate.queryForStream((conn)->{
			StringBuilder sb = new StringBuilder( "select " + 
					"s.designated as s_designated, " + 
					"s.lawcode as s_lawcode, " + 
					"s.sectionnumber as s_sectionnumber " + 
					"from statutecitation s " + 
					"where (s.id) in " );
			sb.append("(");
			for ( int i=0; i < keys.size(); ++i ) {
				sb.append("?,");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			for ( int i=0; i < keys.size(); ++i ) {
				ps.setInt(i+1, keys.get(i));
			}
			return ps;
		}, this::mapStatuteCitation).collect(Collectors.toList());
	}

	private StatuteCitation mapStatuteCitation( ResultSet resultSet, int rowNum) throws SQLException {
		StatuteCitation statuteCitation = new StatuteCitation(new StatuteKey(resultSet.getString("s_lawcode"), resultSet.getString("s_sectionnumber")));
		statuteCitation.setDesignated(resultSet.getBoolean("s_designated"));
		return statuteCitation;
	}

	public List<StatuteCitation> statutesWithReferringOpinions(List<StatuteKey> statuteKeys) {
		return jdbcTemplate.queryForStream((conn)->{
			StringBuilder sb = new StringBuilder( "select" + 
					"s.designated as s_designated, " + 
					"s.lawcode as s_lawcode, " + 
					"s.sectionnumber as s_sectionnumber, " + 
					"osc.countreferences as osc_countreferences, " + 
					"ob.id as oboc_id, " + 
					"ob.countreferringopinions as ob_countreferringopinions, " + 
					"ob.opiniondate as ob_opiniondate, " + 
					"ob.page ob_page, " + 
					"ob.volume ob_volume, " + 
					"ob.vset ob_vset, " + 
					"ob.title ob_title " + 
					"from statutecitation s " + 
					"left outer join opinionstatutecitation osc on osc.statutecitation_id = s.id " + 
					"left outer join opinionbase ob on osc.opinionbase_id = ob.id " + 
					"where (s.lawcode, s.sectionnumber) in " );
			sb.append("(");
			for ( int i=0; i < statuteKeys.size(); ++i ) {
				sb.append("(?,?),");
			}
			sb.deleteCharAt(sb.length()-1);
			sb.append(")");
			PreparedStatement ps = conn.prepareStatement(sb.toString());
			for ( int i=0; i < statuteKeys.size(); ++i ) {
				ps.setString((i+1)*2, statuteKeys.get(i).getLawCode());
				ps.setString((i+1)*2+1, statuteKeys.get(i).getSectionNumber());
			}
			return ps;
		}, this::mapStatuteCitationsWithReferringOpinions).collect(Collectors.groupingBy(StatuteCitation::getId, Collectors.reducing((sc1, sc2)->{
			sc1.getReferringOpinions().addAll(sc2.getReferringOpinions());
			return sc1;
		}))).values().stream().map(Optional::get).collect(Collectors.toList());

	}

	private StatuteCitation mapStatuteCitationsWithReferringOpinions( ResultSet resultSet, int rowNum) throws SQLException {
		StatuteCitation statuteCitation = new StatuteCitation(new StatuteKey(resultSet.getString("s_lawcode"), resultSet.getString("s_sectionnumber")));
		statuteCitation.setDesignated(resultSet.getBoolean("s_designated"));
		statuteCitation.setReferringOpinions(new HashSet<>());
		OpinionBase opinionBaseReferring = new OpinionBase(
				DTYPES.OPINIONBASE, 
				resultSet.getInt("obro_page"), 
				resultSet.getInt("obro_volume"), 
				resultSet.getInt("obro_vset"));
		opinionBaseReferring.setId(Integer.valueOf(resultSet.getString("obro_id")));
		opinionBaseReferring.setCountReferringOpinions(resultSet.getInt("obro_countreferrringopinions"));
		if ( resultSet.getObject("obro_opiniondate") != null ) opinionBaseReferring.setOpinionDate((LocalDate)resultSet.getObject("obro_opiniondate"));
		opinionBaseReferring.setTitle(resultSet.getString("obor_title"));
		OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, opinionBaseReferring, resultSet.getInt("osc_countreferences"));
		statuteCitation.getReferringOpinions().add(opinionStatuteCitation);
		return statuteCitation;
	}
	public void insert(StatuteCitation statute) {
		KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcTemplate.update((conn)->{
			PreparedStatement ps = conn.prepareStatement(
					"insert into statutecitation(designated, lawcode, sectionnumber) values(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			ps.setBoolean(1, statute.getDesignated());
			ps.setString(2, statute.getStatuteKey().getLawCode());
			ps.setObject(3, statute.getStatuteKey().getSectionNumber());
			return ps;
		}, keyHolder);
		statute.setId(keyHolder.getKey().intValue());
	}
}
