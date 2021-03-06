package com.github.karlnicholas.opinionservices.slipopinion.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.stereotype.Component;

@Component
public class OpinionViewDao {
	private final DataSource dataSource;
	
	public OpinionViewDao(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public String getSlipOpinionList() throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("select slipopinionlist from slipopinionlist where id = 1", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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

}
