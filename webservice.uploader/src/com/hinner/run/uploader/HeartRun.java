package com.hinner.run.uploader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.FileUtil;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.ws.api.Service;

import middle.db.operator.dao.entity.Heart;
import middle.db.operator.dao.pstmt.result.HeartDao;

/**
 * 心跳线程，负责线程的采集，上传。将本周期内，最新的心跳数据上传到webservice后台
 * 
 * @author robin
 *
 */
public class HeartRun implements Runnable {
	public static Logger logger = Logger.getLogger(HeartRun.class);

	public static final String PATH_SERIAL_HEART = "configs/heart.serial";

	public HeartRun() {
		super();
	}

	@Override
	public void run() {
		long serial = FileUtil.readSerial(PATH_SERIAL_HEART);
		logger.info("开始心跳周期，上次的serial=[" + serial + "]");
		// 查询需要上传的心跳数据
		// 条件
		// 1.大于serial
		// 2.每个装置的最新数据
		List<Heart> heartList = getHeartList(serial);
		if (heartList == null || heartList.size() == 0) {
			logger.info("本周期采集到的心跳数据为空或者0");
			return;
		}
		// 更新本次查询的最大serial值
		serial = getMaxSerial(heartList);
		FileUtil.saveSerial(PATH_SERIAL_HEART, serial);
		logger.info("更新心跳的serial=[" + serial + "]");

		// 上传心跳数据
		Service service = WSClient.newService();
		logger.info("开始上传心跳数据，总共有[" + heartList.size() + "]心跳数据需要进行上传");
		long t1 = System.currentTimeMillis();
		for (Heart heart : heartList) {
			String fResultStr = "";
			WSClient.addHeartBeat(service, heart.getDtuid(), heart.getRecvttime(), fResultStr);
		}
		logger.info(
				"心跳上传完毕，总共传输了[" + heartList.size() + "]条心跳数据，共耗时[" + ((System.currentTimeMillis() - t1) / 1000) + "]秒");
	}

	private long getMaxSerial(List<Heart> heartList) {
		long max = 0;
		for (Heart heart : heartList) {
			max = heart.getSerial() > max ? heart.getSerial() : max;
		}
		return max;
	}

	/**
	 * 从数据库中获取需要上传的心跳数据
	 * 
	 * @param serial
	 * @return
	 */
	private List<Heart> getHeartList(long serial) {
		Connection conn = null;
		try {
			conn = JDBCConnUtil.getMiddleConn();
			//
			HeartDao dao = new HeartDao();
			return dao.getLatestGroupByDtuGtSerial(conn, serial);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			JDBCConnUtil.close(conn);
		}
		return null;
	}

}
