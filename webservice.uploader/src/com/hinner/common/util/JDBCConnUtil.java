package com.hinner.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class JDBCConnUtil {
	public static Logger logger = Logger.getLogger(JDBCConnUtil.class);

	public static void close(PreparedStatement pstmt) {
		if (pstmt != null)
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public static void close(ResultSet rs) {
		if (rs != null)
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
	}

	public static void close(Connection conn) {
		try {
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取本地sqlite台账的数据连接
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public static Connection getTzConnection() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String path = DeployUtil.getProperties("sqlite.tz.path", "db/tz.sqlite");
		logger.info("使用台帐本地数据库[" + path + "]");
		return DriverManager.getConnection("jdbc:sqlite:" + path);
	}

	public static Connection getMiddleConn() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		String path = DeployUtil.getProperties("sqlite.middle.path", "db/middle.sqlite");
		logger.info("使用middle本地数据库[" + path + "]");
		return DriverManager.getConnection("jdbc:sqlite:" + path);
	}

}
