package com.hinner.sqlite.tz.entity.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.common.util.JDBCConnUtil;
import com.hinner.sqlite.tz.entity.ElePo;

public class ElePoDao {

	public static Logger logger = Logger.getLogger(MLineDao.class);

	public void add(Connection conn, List<ElePo> list) throws SQLException {
		String sql = "insert into ElePo (aMLineID,ElePoID,Serial,LineLen,CompID,TotalLen) values (?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		pstmt = conn.prepareStatement(sql);
		for (ElePo elePo : list) {
			pstmt.setString(1, elePo.getaMLineID());
			pstmt.setString(2, elePo.getElePoID());
			pstmt.setInt(3, elePo.getSerial());
			pstmt.setDouble(4, elePo.getLineLen());
			pstmt.setString(5, elePo.getCompID());
			pstmt.setDouble(6, elePo.getTotalLen());
			pstmt.addBatch();
		}
		int[] r = pstmt.executeBatch();
		logger.info("[" + r.length + "]条数据已经插入ElePo");
	}

	public void delete(Connection conn, String mLineID, String compID) throws SQLException {
		String sql = "delete from ElePo where aMLineID=? and CompID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mLineID);
			pstmt.setString(2, compID);
			int r = pstmt.executeUpdate();
			logger.info("[" + r + "]条数据已经删除ElePo");
		} finally {
			JDBCConnUtil.close(pstmt);
		}
	}

	/**
	 * 删除线路id为mLineID的所有数据
	 * 
	 * @param conn
	 * @param mLineID
	 * @throws SQLException
	 */
	public void delete(Connection conn, String mLineID) throws SQLException {
		String sql = "delete from ElePo where aMLineID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, mLineID);
			int r = pstmt.executeUpdate();
			logger.info("[" + r + "]条数据已经删除ElePo");
		} finally {
			JDBCConnUtil.close(pstmt);
		}
	}

}
