package com.hinner.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hinner.common.util.JDBCConnUtil;

import middle.db.operator.dao.entity.Heart;
import middle.db.operator.dao.pstmt.result.HeartLatestDao;

public class MiddleTest {
	public static Logger logger = Logger.getLogger(MiddleTest.class);

	public void testQueryHeart() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		try {
			logger.info("尝试获取middle库的连接");
			conn = JDBCConnUtil.getMiddleConn();
			logger.info("尝试查询heart_latest表");
			HeartLatestDao dao = new HeartLatestDao();
			Map<String, Heart> map = dao.getLatestHeartMap(conn);
			logger.info("查询到[" + map.size() + "]条数据.");
		} finally {
			JDBCConnUtil.close(conn);
		}
	}

}
