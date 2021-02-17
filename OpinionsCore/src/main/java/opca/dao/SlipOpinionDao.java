package opca.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import opca.model.OpinionBase;
import opca.model.OpinionKey;
import opca.model.OpinionStatuteCitation;
import opca.model.SlipOpinion;
import opca.model.SlipProperties;
import opca.model.StatuteCitation;

@Service
public class SlipOpinionDao {
	private final SlipPropertiesDao slipPropertiesDao;
	private final JdbcTemplate jdbcTemplate;

	public SlipOpinionDao(
			SlipPropertiesDao slipPropertiesDao, 
			JdbcTemplate jdbcTemplate
	) {
		this.jdbcTemplate = jdbcTemplate;
		this.slipPropertiesDao = slipPropertiesDao;
	}

	public List<SlipOpinion> loadOpinionsWithJoins() {
		// TODO Auto-generated method stub
		return null;
	}
//	@Query("select s from SlipOpinions s left outer join fetch s.statuteCitations left outer join fetch s.opinionCitations left outer join fetch s.referringOpinions")
//	@EntityGraph(attributePaths = {"statuteCitations", "opinionCitations", "referringOpinions", })
//	List<SlipOpinion> loadOpinionsWithJoins();

//	@NamedEntityGraphs({ 
//		@NamedEntityGraph(name="fetchGraphForOpinionsWithJoins", attributeNodes= {
//			@NamedAttributeNode(value="statuteCitations", subgraph="fetchGraphForOpinionsWithJoinsPartB"), 
//		}, 
//		subgraphs= {
//			@NamedSubgraph(
//				name = "fetchGraphForOpinionsWithJoinsPartB", 
//				attributeNodes = { @NamedAttributeNode(value = "statuteCitation") } 
//			),
//		}), 	

	public List<SlipOpinion> loadOpinionsWithJoinsForKeys(List<OpinionKey> opinionKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<SlipOpinion> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void save(SlipOpinion slipOpinion) {
		// TODO Auto-generated method stub
		
	}

	public void delete(SlipOpinion deleteOpinion) {
		// TODO Auto-generated method stub
		
	}

	public List<SlipOpinion> selectAllForView() {
		// just get all slip opinions
		List<SlipOpinion> opinions = jdbcTemplate.queryForStream(loadAllSlipOpinionsQuery, this::decodeSelectForView)
	        .parallel()
	        .collect(Collectors.groupingBy(SlipOpinion::getId, Collectors.reducing((sp1, sp2)->{
					sp1.mergePublishedOpinion(sp2);
					return sp1;
				})))
	        .values().stream().map(Optional::get)
	        .collect(Collectors.toList());
		
		List<SlipProperties> spl = slipPropertiesDao.findAll();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}
	
	public List<SlipOpinion> selectFromKeysForView(List<OpinionKey> opinionKeys) {
		// just get all slip opinions
		StringBuilder sb = new StringBuilder(loadSlipOpinionsForKeysQuery);
		sb.append("(");
		for ( int i=0; i < opinionKeys.size(); ++i) {
			sb.append("?,");
		}
		sb.deleteCharAt(sb.length()-1);
		sb.append(")");
		List<SlipOpinion> opinions = jdbcTemplate.queryForStream(sb.toString(), (preparedStatement)->{
			for ( int i=0; i < opinionKeys.size(); ++i ) {
				preparedStatement.setString(i, opinionKeys.get(i).getVSetAsString());
			}
		}, this::decodeSelectForView)
	        .parallel()
	        .collect(Collectors.groupingBy(SlipOpinion::getId, Collectors.reducing((sp1, sp2)->{
					sp1.mergePublishedOpinion(sp2);
					return sp1;
				})))
	        .values().stream().map(Optional::get)
	        .collect(Collectors.toList());
		
		List<SlipProperties> spl = slipPropertiesDao.findAll();
		for ( SlipOpinion slipOpinion: opinions ) {
			slipOpinion.setSlipProperties(spl.get(spl.indexOf(new SlipProperties(slipOpinion))));
		}
		return opinions;
	}

	private final String loadAllSlipOpinionsQuery="select " + 
			"o.id as o_id," + 
			"o.opiniondate as o_opiniondate," + 
			"o.page as o_page," + 
			"o.volume as o_volume," + 
			"o.vset as o_vset," + 
			"o.title as o_title," + 
			"osc.countreferences as osc_countreferences," + 
			"sc.designated as sc_designated," + 
			"sc.lawcode as sc_lawcode," + 
			"sc.sectionnumber as sc_sectionnumber," + 
			"oc.countreferringopinions as oc_countreferringopinions," + 
			"oc.opiniondate as oc_opiniondate," + 
			"oc.page as oc_page," + 
			"oc.volume as oc_volume," + 
			"oc.vset as oc_vset," + 
			"oc.title as oc_title," + 
			"ocosc.countreferences as ocosc_countreferences," + 
			"ocsc.id as ocsc_id," + 
			"ocsc.designated as ocsc_designated," + 
			"ocsc.lawcode as ocsc_lawcode," + 
			"ocsc.sectionnumber as ocsc_sectionnumber," + 
			"sp.filename," + 
			"sp.fileextension" + 
			"from opinionbase o " + 
			"left outer join opinionstatutecitation osc on osc.opinionbase_id = o.id " + 
			"left outer join statutecitation sc on sc.id = osc.statutecitation_id" + 
			"left outer join opinionbase_opinioncitations oboc on oboc.referringopinions_id = o.id" + 
			"left outer join opinionbase oc on oc.id = oboc.opinioncitations_id" + 
			"left outer join opinionstatutecitation ocosc on  ocosc.opinionbase_id = oc.id " + 
			"left outer join statutecitation ocsc on ocsc.id = ocosc.statutecitation_id" + 
			"join slipproperties sp on o.id = sp.slipopinion_id" + 
			"where o.dtype=-1344462334";

	private final String loadSlipOpinionsForKeysQuery="select " + 
			"o.id as o_id," + 
			"o.opiniondate as o_opiniondate," + 
			"o.page as o_page," + 
			"o.volume as o_volume," + 
			"o.vset as o_vset," + 
			"o.title as o_title," + 
			"osc.countreferences as osc_countreferences," + 
			"sc.designated as sc_designated," + 
			"sc.lawcode as sc_lawcode," + 
			"sc.sectionnumber as sc_sectionnumber," + 
			"oc.countreferringopinions as oc_countreferringopinions," + 
			"oc.opiniondate as oc_opiniondate," + 
			"oc.page as oc_page," + 
			"oc.volume as oc_volume," + 
			"oc.vset as oc_vset," + 
			"oc.title as oc_title," + 
			"ocosc.countreferences as ocosc_countreferences," + 
			"ocsc.id as ocsc_id," + 
			"ocsc.designated as ocsc_designated," + 
			"ocsc.lawcode as ocsc_lawcode," + 
			"ocsc.sectionnumber as ocsc_sectionnumber," + 
			"sp.filename," + 
			"sp.fileextension" + 
			"from opinionbase o " + 
			"left outer join opinionstatutecitation osc on osc.opinionbase_id = o.id " + 
			"left outer join statutecitation sc on sc.id = osc.statutecitation_id" + 
			"left outer join opinionbase_opinioncitations oboc on oboc.referringopinions_id = o.id" + 
			"left outer join opinionbase oc on oc.id = oboc.opinioncitations_id" + 
			"left outer join opinionstatutecitation ocosc on  ocosc.opinionbase_id = oc.id " + 
			"left outer join statutecitation ocsc on ocsc.id = ocosc.statutecitation_id" + 
			"join slipproperties sp on o.id = sp.slipopinion_id" + 
			"where o.dtype=-1344462334 and o_id in " + 
			"";

	private SlipOpinion decodeSelectForView(ResultSet resultSet, int rowNum) throws SQLException {
		SlipOpinion slipOpinion = new SlipOpinion(
				resultSet.getString("filename"), 
				resultSet.getString("fileextension"), 
				resultSet.getString("o_title"), 
				(LocalDate)resultSet.getObject("o_opiniondate"), 
				"court", 
				"searchUrl"
			);
		slipOpinion.setId(resultSet.getInt("o_id"));
		slipOpinion.setStatuteCitations(new HashSet<>());
		slipOpinion.setOpinionCitations(new HashSet<>());
		// do StatuteCitation
		if ( resultSet.getString("sc_lawcode") != null && resultSet.getString("sc_sectionnumber") != null ) {
			StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
					resultSet.getString("sc_lawcode"), 
					resultSet.getString("sc_sectionnumber"));
			statuteCitation.setDesignated(Boolean.parseBoolean(resultSet.getString("sc_designated")));
			OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
					resultSet.getInt("osc_countreferences"));
			if ( !slipOpinion.getStatuteCitations().contains(opinionStatuteCitation)) {
				slipOpinion.getStatuteCitations().add(opinionStatuteCitation); 
			}
		}
		if ( resultSet.getRef("oc_page") != null && resultSet.getRef("oc_volume") != null && resultSet.getRef("oc_vset") != null ) {
			// do OpinionCitation
			OpinionKey opinionKey = new OpinionKey(
					resultSet.getInt("oc_page"), 
					resultSet.getInt("oc_volume"), 
					resultSet.getInt("oc_vset")
				);
			OpinionBase opinionCitation = new OpinionBase(opinionKey);
			OpinionBase finalOpinionCitation = slipOpinion.getOpinionCitations().stream().filter(oc->oc.equals(opinionCitation)).findAny().orElseGet(
				()->{
					try {
					opinionCitation.setCountReferringOpinions(resultSet.getInt("oc_countreferringopinions"));
					if ( resultSet.getRef("oc_opiniondate") != null ) {
						opinionCitation.setOpinionDate((LocalDate)resultSet.getObject("oc_opiniondate"));
					}
					opinionCitation.setStatuteCitations(new HashSet<>());
					slipOpinion.getOpinionCitations().add(opinionCitation); 
					return opinionCitation;
					} catch ( Exception e ) {throw new RuntimeException(e);}
				});
			// do OpinionCitation->StatuteCitation
			if ( resultSet.getRef("ocsc_lawcode") != null && resultSet.getRef("ocsc_sectionnumber") != null ) {
				StatuteCitation statuteCitation = new StatuteCitation(slipOpinion, 
						resultSet.getString("ocsc_lawcode"), 
						resultSet.getString("ocsc_sectionnumber"));
				statuteCitation.setDesignated(Boolean.parseBoolean(resultSet.getString("ocsc_designated")));
				OpinionStatuteCitation opinionStatuteCitation = new OpinionStatuteCitation(statuteCitation, slipOpinion, 
						resultSet.getInt("ocosc_countreferences"));		

				if ( !finalOpinionCitation.getStatuteCitations().contains(opinionStatuteCitation)) {
					finalOpinionCitation.getStatuteCitations().add(opinionStatuteCitation); 
				}
			}
		}
		
		return slipOpinion;
	}
	
}
