package com.hinner.sqlite.tz.entity.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.common.util.JDBCConnUtil;
import com.hinner.sqlite.tz.entity.Dtu;

public class DtuDao {

	public static Logger logger = Logger.getLogger(DtuDao.class);

	/**
	 * 根据线路编号删除DTU
	 * 
	 * @param conn
	 * @param mLineID
	 * @throws SQLException
	 */
	public void delete(Connection conn, String mLineID) throws SQLException {
		String sql = "delete from DTU where mLineID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mLineID);
			int r = pstmt.executeUpdate();
			logger.info("DTU:[" + r + "]条数据.删除.");
		} finally {
			JDBCConnUtil.close(pstmt);
		}
	}

	/**
	 * 批量添加设备
	 * 
	 * @param conn
	 * @param dtuList
	 * @throws SQLException
	 */
	public void add(Connection conn, List<Dtu> dtuList) throws SQLException {
		String sql = "insert into DTU (dtuid,mLineID,elePoID,sLineID,compID,absulateDis) values (?,?,?,?,?,?) ";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (Dtu dtu : dtuList) {
				pstmt.setString(1, dtu.getDtuid());
				pstmt.setString(2, dtu.getmLineID());
				pstmt.setString(3, dtu.getElePoID());
				pstmt.setInt(4, dtu.getsLineID());
				pstmt.setString(5, "");
				pstmt.setDouble(6, dtu.getAbsulateDis());
				//
				pstmt.addBatch();
			}
			int[] r = pstmt.executeBatch();
			logger.info("DTU:[" + r.length + "]条数据.新增.");
		} finally {
			JDBCConnUtil.close(pstmt);
		}
	}

}
