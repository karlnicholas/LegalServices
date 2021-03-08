package com.github.karlnicholas.opinionservices.slipopinion.dao;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

	public Integer insertOpinionView(byte[] opinionViewBytes, LocalDate date) throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("insert into opinionview(opiniondate, opinionview) values(?,?)", Statement.RETURN_GENERATED_KEYS);
		) {
			ps.setObject(1, date);
			ps.setBytes(2, opinionViewBytes);
			ps.executeUpdate();
			ResultSet rs = ps.getGeneratedKeys();
			rs.next();
			return rs.getInt(1);
		}
	}

	public List<ByteBuffer> getOpinionViews() throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("select opinionview from opinionview" );
		) {
			List<ByteBuffer> opinionViews = new ArrayList<>();
			ResultSet rs = ps.executeQuery();
			while ( rs.next() ) {
				byte[] bytes = rs.getBytes(1);
				opinionViews.add(ByteBuffer.wrap(bytes));
			}
			return opinionViews; 
		}
	}

	public byte[] getOpinionViewBytesForId(Integer id) throws SQLException {
		try (Connection con = dataSource.getConnection();
			 PreparedStatement ps = con.prepareStatement("select opinionview from opinionview where id = ?" );
		) {
			ps.setInt(1, id);
			ResultSet rs = ps.executeQuery();
			rs.next();
			return rs.getBytes(1);
		}
	}
}
