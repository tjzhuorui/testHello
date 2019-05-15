package com.hinner.sqlite.tz.entity.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.common.util.JDBCConnUtil;
import com.hinner.sqlite.tz.entity.MLine;

public class MLineDao {

	public static Logger logger = Logger.getLogger(MLineDao.class);

	public void delete(Connection conn, String lineID) throws SQLException {
		String sql = "delete from MLine where MLineID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, lineID);
			int r = pstmt.executeUpdate();
			logger.info("[" + r + "]条数据已经删除MLineList");
		} finally {
			JDBCConnUtil.close(pstmt);
		}
	}

	public void add(Connection conn, List<MLine> linelist) throws SQLException {
		String sql = "insert into MLine (MLineID,MLName,Volevel,Station1,Station2,CompID,TotalLen) values (?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql);
		for (MLine line : linelist) {
			pstmt.setString(1, line.getMLineID());
			pstmt.setString(2, line.getMLName());
			pstmt.setString(3, line.getVolevel());
			pstmt.setString(4, line.getStation1());
			pstmt.setString(5, line.getStation2());
			pstmt.setString(6, "");
			pstmt.setDouble(7, line.getTotalLen());
			pstmt.addBatch();
		}
		int[] r = pstmt.executeBatch();
		logger.info("[" + r.length + "]条数据已经插入MLineList");
	}

	public void add(Connection conn, MLine line) throws SQLException {
		String sql = "insert into MLine (MLineID,MLName,Volevel,Station1,Station2,CompID,TotalLen) values (?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql);
		pstmt.setString(1, line.getMLineID());
		pstmt.setString(2, line.getMLName());
		pstmt.setString(3, line.getVolevel());
		pstmt.setString(4, line.getStation1());
		pstmt.setString(5, line.getStation2());
		pstmt.setString(6, "");
		pstmt.setDouble(7, line.getTotalLen());
		int r = pstmt.executeUpdate();
		logger.info("[" + r + "]条数据已经插入MLineList");
	}

	public List<MLine> getMLineList(Connection conn) throws SQLException {
		List<MLine> list = new ArrayList<MLine>();
		String sql = "select MLineID,MLName,Volevel,Station1,Station2,CompID,TotalLen from MLine";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				MLine line = new MLine();
				list.add(line);
				//
				line.setMLineID(rs.getString("MLineID"));
				line.setMLName(rs.getString("MLName"));
				line.setVolevel(rs.getString("Volevel"));
				line.setStation1(rs.getString("Station1"));
				line.setStation2(rs.getString("Station2"));
				line.setTotalLen(rs.getDouble("TotalLen"));
			}
		} finally {
			JDBCConnUtil.close(rs);
			JDBCConnUtil.close(pstmt);
		}
		return list;
	}

}
