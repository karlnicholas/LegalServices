package com.github.karlnicholas.legalservices.opinionrestca;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.github.karlnicholas.legalservices.caselist.model.CASELISTSTATUS;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntries;
import com.github.karlnicholas.legalservices.caselist.model.CaseListEntry;
import com.github.karlnicholas.legalservices.opinion.model.*;
import com.github.karlnicholas.legalservices.statute.StatuteKey;

public class OpinionBaseDao {
	private final DataSource dataSource;
	
	public OpinionBaseDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 *  
	 * @param opinionKeys
	 * @return
	 * @throws SQLException 
	 */
	public List<OpinionBase> getOpinionsWithStatuteCitations(List<OpinionKey> opinionKeys) throws SQLException {
		if ( opinionKeys.size() == 0 )
			return Collections.emptyList();
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
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement(sb.toString(), ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		) {
			for ( int i=0; i < opinionKeys.size(); ++i ) {
				ps.setInt(i*3+1, opinionKeys.get(i).getVolume());
				ps.setInt(i*3+2, opinionKeys.get(i).getVset());
				ps.setInt(i*3+3, opinionKeys.get(i).getPage());
			}
			try (ResultSet rs = ps.executeQuery()) {
				return new ResultSetIterable<OpinionBase>(rs, rs2 -> mapOpinionsWithStatuteCitations(rs2)).stream()
						.collect(Collectors.groupingBy(OpinionBase::getId, Collectors.reducing((ob1, ob2)->{
							ob1.getStatuteCitations().addAll(ob2.getStatuteCitations());
							return ob1;
						}))).values().stream().map(Optional::get).collect(Collectors.toList());
			}
			
		}
	}

	private OpinionBase mapOpinionsWithStatuteCitations(ResultSet resultSet) throws SQLException {
		OpinionBase opinionBase = new OpinionBase(
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

	public String callSlipOpinionUpdateNeeded() throws SQLException {
		try (Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareCall("{call checkSlipOpinionUpdate()}");
		) {
			try (ResultSet rs = ps.executeQuery()) {
				rs.next();
				return rs.getString(1);
			}
		}
	}
	public void updateSlipOpinionList(String string) throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("update slipopinionlist set slipopinionlist=? where id = 1" );
		) {
			ps.setString(1, string);
			ps.executeUpdate();
		}
	}

	public CaseListEntries caseListEntries() throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("select * from caselistentry", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
		) {
			try (ResultSet rs = ps.executeQuery()) {
				return new CaseListEntries(new ResultSetIterable<CaseListEntry>(rs, rs2 -> mapCaseListEntry(rs2)).stream().collect(Collectors.toList()));
			}
		}
	}
	private CaseListEntry mapCaseListEntry(ResultSet resultSet) throws SQLException {
		return CaseListEntry.builder()
				.id(resultSet.getString("id"))
				.fileName(resultSet.getString("filename"))
				.fileExtension(resultSet.getString("fileExtension"))
				.title(resultSet.getString("title"))
				.opinionDate(((Date)resultSet.getObject("opiniondate")).toLocalDate())
				.postedDate(((Date)resultSet.getObject("posteddate")).toLocalDate())
				.court(resultSet.getString("court"))
				.searchUrl(resultSet.getString("searchurl"))
				.status(CASELISTSTATUS.valueOf(resultSet.getString("status")))
				.build();
	}

	public void caseListEntryUpdates(CaseListEntries caseListEntries) throws SQLException {
		try (Connection con = dataSource.getConnection(); 
			PreparedStatement pss = con.prepareStatement("select * from caselistentry", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			PreparedStatement psi = con.prepareStatement("insert into caselistentry(id, filename, fileextension, title, opiniondate, posteddate, court, searchurl, status) values(?,?,?,?,?,?,?,?,?)");
			PreparedStatement psu = con.prepareStatement("update caselistentry set status = ? where id = ?");
			PreparedStatement psd = con.prepareStatement("delete from caselistentry where id = ?");
		) {
			con.setAutoCommit(false);
			try (ResultSet rs = pss.executeQuery()) {
				List<CaseListEntry> existingEntries = new ResultSetIterable<CaseListEntry>(rs, rs2 -> mapCaseListEntry(rs2)).stream().collect(Collectors.toList());
				List<CaseListEntry> deleteEntries = new ArrayList<>();
				Iterator<CaseListEntry> cleIt = caseListEntries.iterator();
				while ( cleIt.hasNext()) {
					CaseListEntry cle = cleIt.next();
					if ( cle.getStatus() == CASELISTSTATUS.DELETED ) {
						cleIt.remove();
						deleteEntries.add(cle);
					} else if ( existingEntries.contains(cle)) {
						cleIt.remove();
						if ( cle.getStatus() == existingEntries.get(existingEntries.indexOf(cle)).getStatus()) {
							existingEntries.remove(cle);
						} else {
							existingEntries.get(existingEntries.indexOf(cle)).setStatus(cle.getStatus());
						}
					}
				}
				for ( CaseListEntry caseListEntry: caseListEntries) {
					psi.setString(1, caseListEntry.getId());
					psi.setString(2, caseListEntry.getFileName());
					psi.setString(3, caseListEntry.getFileExtension());
					psi.setString(4, caseListEntry.getTitle());
					psi.setObject(5, caseListEntry.getOpinionDate());
					psi.setObject(6, caseListEntry.getPostedDate());
					psi.setString(7, caseListEntry.getCourt());
					psi.setString(8, caseListEntry.getSearchUrl());
					psi.setString(9, caseListEntry.getStatus().name());
					psi.addBatch();
				}
				psi.executeBatch();
				for ( CaseListEntry caseListEntry: existingEntries) {
					psu.setString(1, caseListEntry.getStatus().name());
					psu.setString(2, caseListEntry.getId());
					psu.addBatch();
				}
				psu.executeBatch();
				for ( CaseListEntry caseListEntry: deleteEntries) {
					psd.setString(1, caseListEntry.getId());
					psd.addBatch();
				}
				psd.executeBatch();
			}
			con.commit();
		}
	}
		
//	id varchar(32), filename varchar(31), fileextension varchar(7), title varchar(137), opiniondate datetime, posteddate datetime, court varchar(15), searchurl varchar(128), status varchar(15) not null, retrycount integer
	public void caseListEntryUpdate(CaseListEntry caseListEntry) throws SQLException {
		try (Connection con = dataSource.getConnection();
			PreparedStatement ps = con.prepareStatement("update caselistentry set status = ? where id = ?");
		) {
			ps.setString(1, caseListEntry.getStatus().name());
			ps.setString(2, caseListEntry.getId());
			ps.executeUpdate();
		}
	}

}
