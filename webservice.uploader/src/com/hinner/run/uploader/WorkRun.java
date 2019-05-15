package com.hinner.run.uploader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.DateUtil;
import com.hinner.common.util.FileUtil;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.ws.api.Service;

import middle.db.operator.dao.entity.Work;
import middle.db.operator.dao.pstmt.result.WorkDao;

/**
 * 线程
 * 
 * @author robin
 *
 */
public class WorkRun implements Runnable {

	public static Logger logger = Logger.getLogger(WorkRun.class);

	public static final String PATH_SERIAL_WORK = "configs/work.serial";

	public WorkRun() {
		super();
	}

	@Override
	public void run() {
		long serial = FileUtil.readSerial(PATH_SERIAL_WORK);
		// 逻辑同心跳流程
		logger.info("开始工况周期，serial=[" + serial + "]");
		List<Work> workList = getWorkList(serial);
		if (workList == null || workList.size() == 0) {
			logger.info("本周期采集到的工况数据为空或者0");
			return;
		}
		// 更新本次查询的最大serial值
		serial = getMaxSerial(workList);
		FileUtil.saveSerial(PATH_SERIAL_WORK, serial);
		logger.info("更新工况的serial=[" + serial + "]");

		// 上传工况数据
		Service service = WSClient.newService();
		logger.info("开始上传工况数据，总共有[" + workList.size() + "]工况数据需要进行上传");
		long t1 = System.currentTimeMillis();
		for (Work work : workList) {
			String fResultStr = "";
			WSClient.addWorkLog(service, work.getDtuid(), DateUtil.our2their(work.getTaketime()), work.getPowerstat(),
					1.0 * (work.getBatteryvolt() / 1000), work.getSensortemp() * 1.0, 1.0 * work.getCurr(), fResultStr);
		}
		logger.info(
				"工况上传完毕，总共传输了[" + workList.size() + "]条工况数据，共耗时[" + ((System.currentTimeMillis() - t1) / 1000) + "]秒");
	}

	private int getMaxSerial(List<Work> workList) {
		int max = 0;
		for (Work work : workList) {
			if (work.getSerial() > max)
				max = work.getSerial();
		}
		return max;
	}

	/**
	 * 从数据库获取需要进行上传的工况数据
	 * 
	 * @param serial
	 * @return
	 */
	private List<Work> getWorkList(long serial) {
		Connection conn = null;
		try {
			conn = JDBCConnUtil.getMiddleConn();
			//
			WorkDao dao = new WorkDao();
			return dao.getLatestGroupByDtuGtSerial(conn, serial);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			JDBCConnUtil.close(conn);
		}
		return null;
	}

}
