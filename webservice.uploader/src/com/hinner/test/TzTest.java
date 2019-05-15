package com.hinner.test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.common.util.JDBCConnUtil;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.sqlite.tz.entity.dao.MLineDao;

public class TzTest {
	public static Logger logger = Logger.getLogger(TzTest.class);

	public void testQueryLine() throws ClassNotFoundException, SQLException {
		Connection conn = null;
		try {
			logger.info("开始获取tz库的数据库连接");
			conn = JDBCConnUtil.getTzConnection();
			logger.info("查询MLine表的线路信息(历史)");
			MLineDao dao = new MLineDao();
			List<MLine> lineList = dao.getMLineList(conn);
			logger.info("查询到[" + lineList.size() + "]条记录");
		} finally {
			JDBCConnUtil.close(conn);
		}

	}

}
