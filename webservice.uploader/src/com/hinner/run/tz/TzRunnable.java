package com.hinner.run.tz;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.mem.cache.TzMemCache;
import com.hinner.sqlite.tz.entity.Dtu;
import com.hinner.sqlite.tz.entity.ElePo;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.sqlite.tz.entity.dao.DtuDao;
import com.hinner.sqlite.tz.entity.dao.ElePoDao;
import com.hinner.sqlite.tz.entity.dao.MLineDao;
import com.hinner.ws.api.Service;

public class TzRunnable implements Runnable {

	public static Logger logger = Logger.getLogger(TzRunnable.class);

	public TzRunnable() {
		super();
	}

	public void run() {
		Service service = WSClient.newService();
		// for (Company company : companyList) {
		// 同时检查线路、杆塔、设备表的更新时间，一旦有更新，则全部台账都要进行更新
		// 线路
		String lineServerUpdateTime = TzTableCommon.getServerUpdateTime(service, TzTableCommon.TABLE_MLine);
		String lineLocalUpdateTime = TzMemCache.getLocalUpdateTime(TzTableCommon.TABLE_MLine);
		logger.info("线路:lineServerUpdateTime=[" + lineServerUpdateTime + "],lineLocalUpdateTime=[" + lineLocalUpdateTime
				+ "]");
		// 杆塔
		String elePoServerUpdateTime = TzTableCommon.getServerUpdateTime(service, TzTableCommon.TABLE_ElePo);
		String elePoLocalUpdateTime = TzMemCache.getLocalUpdateTime(TzTableCommon.TABLE_ElePo);
		logger.info("杆塔:elePoServerUpdateTime=[" + elePoServerUpdateTime + "],elePoLocalUpdateTime=["
				+ elePoLocalUpdateTime + "]");
		// 设备
		String dtuServerUpdateTime = TzTableCommon.getServerUpdateTime(service, TzTableCommon.TABLE_DTU);
		String dtuLocalUpdateTime = TzMemCache.getLocalUpdateTime(TzTableCommon.TABLE_DTU);
		logger.info(
				"设备:dtuServerUpdateTime=[" + dtuServerUpdateTime + "],dtuLocalUpdateTime=[" + dtuLocalUpdateTime + "]");
		// 判断是否需要更新台账，只要有一个需要更新，那么就全部更新
		if (!TzTableCommon.needUpdate(lineServerUpdateTime, lineLocalUpdateTime, elePoServerUpdateTime,
				elePoLocalUpdateTime, dtuServerUpdateTime, dtuLocalUpdateTime)) {
			logger.info("本台账轮训周期，没有台账需要进行更新");
			return;
		}
		logger.info("需要更新台账，准备开始更新台账");
		// 从服务器获取所有的线路信息
		String linename = "";// 查找所有线路
		List<MLine> linelist = TzMLine.getMlineListFromServer(service, linename);
		for (MLine line : linelist) {// 依次遍历线路，针对每条线路做以下操作
			// ---获取杆塔信息
			List<ElePo> elePoList = TzElePo.getElePoListFromServer(service, line);
			// ---按序号排序
			TzElePo.sort(elePoList);
			// ---计算每级杆塔的累计档距,并获得线路的总长度
			double lineTotalLen = TzElePo.calcAccumulateDis(elePoList);
			// ---设置本线路的总长度
			line.setTotalLen(lineTotalLen);
			// ---获取所有设备的台账信息
			List<Dtu> dtuList = TzDtu.getDtuListFromServer(service, line);
			// ---查询台账所处位置累计档距为多少,并将查询之填入到dtu里面
			TzElePo.queryDtuAccumulateDis(dtuList, elePoList);
			// 打印台账整理结果
			printTZ(line, elePoList, dtuList);
			// ---更新数据.-->数据库-->缓存-->缓存中的本地更新时间
			try {
				updateOneLine2DB(line, elePoList, dtuList);
			} catch (ClassNotFoundException e) {
				logger.error("台帐无法保存到本地，但请放心，内存中还是有的.\n" + e.getMessage(), e);
			}
			TzMemCache.updateOneLine2Cache(line, elePoList, dtuList);
		}
		// 缓存中的本地更新时间
		TzMemCache.updateLocalTime(lineServerUpdateTime, elePoServerUpdateTime, dtuServerUpdateTime);
		// }
	}

	private void printTZ(MLine line, List<ElePo> elePoList, List<Dtu> dtuList) {
		//
		logger.info("台账整理完毕");
		//
		logger.info("线路信息." + line);
		//
		StringBuilder sb = new StringBuilder("杆塔信息:\n");
		for (ElePo elePo : elePoList) {
			sb.append(elePo).append("\n");
		}
		logger.info(sb);
		//
		sb = new StringBuilder("设备信息:\n");
		for (Dtu dtu : dtuList) {
			sb.append(dtu).append("\n");
		}
		logger.info(sb);
	}

	/**
	 * 存储一条线路的台账信息，存储的顺序应该是: -->数据库-->缓存-->缓存中的本地更新时间。使用短连接，用完即关闭
	 * 
	 * @param line
	 *            线路信息
	 * @param elePoList
	 *            杆塔信息列表
	 * @param dtuList
	 *            设备信息列表
	 * @throws ClassNotFoundException
	 */
	private void updateOneLine2DB(MLine line, List<ElePo> elePoList, List<Dtu> dtuList) throws ClassNotFoundException {
		Connection conn = null;
		try {
			conn = JDBCConnUtil.getTzConnection();
			conn.setAutoCommit(false);
			// 线路
			MLineDao linedao = new MLineDao();
			linedao.delete(conn, line.getMLineID());
			linedao.add(conn, line);

			// 杆塔
			ElePoDao elePoDao = new ElePoDao();
			elePoDao.delete(conn, line.getMLineID());
			elePoDao.add(conn, elePoList);

			// 设备
			DtuDao dtudao = new DtuDao();
			dtudao.delete(conn, line.getMLineID());
			dtudao.add(conn, dtuList);

			// 事务提交
			conn.commit();
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			try {
				// 事务回滚
				conn.rollback();
			} catch (SQLException e1) {
				logger.error(e.getMessage(), e);
			}
		} finally {
			try {
				conn.setAutoCommit(true);
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			}
			JDBCConnUtil.close(conn);
		}

	}

	public void init() {
		logger.info("运行前，初始化，先进行台账的加载");
		run();
	}

}
