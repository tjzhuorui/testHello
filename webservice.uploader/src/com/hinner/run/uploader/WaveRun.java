package com.hinner.run.uploader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.DateUtil;
import com.hinner.common.util.FileUtil;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.ws.api.Service;

import middle.db.operator.dao.entity.Wave;
import middle.db.operator.dao.entity.WaveData;
import middle.db.operator.dao.entity.WaveDataGp;
import middle.db.operator.dao.entity.WaveDataXb;
import middle.db.operator.dao.pstmt.result.WaveDao;
import middle.db.operator.dao.pstmt.result.WaveDataGpDao;
import middle.db.operator.dao.pstmt.result.WaveDataXbDao;

/**
 * 波形上传者
 * 
 * @author robin
 *
 */
public class WaveRun implements Runnable {
	public static Logger logger = Logger.getLogger(WaveRun.class);

	public static final String PATH_SERIAL_WAVE = "configs/wave.serial";

	public WaveRun() {
		super();
	}

	@Override
	public void run() {
		Connection conn = null;
		try {
			conn = JDBCConnUtil.getMiddleConn();
			doRun(conn);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			JDBCConnUtil.close(conn);
		}
	}

	private void doRun(Connection conn) throws SQLException {
		long serial = FileUtil.readSerial(PATH_SERIAL_WAVE);
		logger.info("开始波形周期，serial=[" + serial + "]");
		// 查询需要上传的波形数据
		// 条件
		// 1.大于serial
		// 2.每个装置的最新数据
		String begintime = DateUtil.getYesterdayDate();
		WaveDao dao = new WaveDao();
		logger.info("准备查询tbl_dat_wave.begintime=[" + begintime + "]");
		List<Wave> waveList = dao.getWaveListGtSerial(conn, serial, begintime);
		if (waveList == null || waveList.size() == 0) {
			logger.info("本周期采集到的波形数据为空或者0");
			return;
		}
		//
		// 更新本次查询的最大serial值
		serial = getMaxSerial(waveList);
		FileUtil.saveSerial(PATH_SERIAL_WAVE, serial);
		logger.info("更新波形的serial=[" + serial + "]");

		// 上传波形数据
		Service service = WSClient.newService();
		logger.info("开始上传波形数据，总共有[" + waveList.size() + "]波形数据需要进行上传");
		long t1 = System.currentTimeMillis();
		for (Wave wave : waveList) {
			// 行波
			uploadXB(conn, service, wave);
			// 工频
			uploadGP(conn, service, wave);
		}
		logger.info(
				"波形上传完毕，总共传输了[" + waveList.size() + "]条波形数据，共耗时[" + ((System.currentTimeMillis() - t1) / 1000) + "]秒");

	}

	private void uploadXB(Connection conn, Service service, Wave wave) throws SQLException {
		// 查询行波数据
		List<Integer> pointArr = getXbPointArr(conn, wave);

		// 上传
		String fDTUID = wave.getDtuid();//
		String fAheadTime = DateUtil.our2their(wave.getTrigger_time(), wave.getTrigger_ms(), wave.getTrigger_us(),
				wave.getTrigger_ns());
		String fWaveBatchID = getXbWaveBatchID(wave.getDtuid(), fAheadTime);
		String fWaveData = list2Csv(pointArr);
		logger.info("行波fWaveData=[" + fWaveData + "]");
		int fDataLen = pointArr.size();
		int fSampRate = wave.getXb_take_fraq();// 采样率
		String fResultStr = "";
		WSClient.commitDTUWave_XB(service, fDTUID, fWaveBatchID, fAheadTime, fWaveData, fDataLen, fSampRate,
				fResultStr);
		// 上传性质
		int fRelaFlag = wave.getTrigger_type();
		//
		WSClient.commitWaveRelaFlag(service, fWaveBatchID, fRelaFlag, fResultStr);
	}

	private void uploadGP(Connection conn, Service service, Wave wave) throws SQLException {
		// 查询行波数据
		List<Integer> pointArr = getGpPointArr(conn, wave);

		// 上传
		String fDTUID = wave.getDtuid();//
		String fAheadTime = DateUtil.our2their(wave.getTrigger_time(), wave.getTrigger_ms(), wave.getTrigger_us(),
				wave.getTrigger_ns());
		String fWaveBatchID = getGpWaveBatchID(wave.getDtuid(), fAheadTime);
		String fWaveData = list2Csv(pointArr);
		logger.info("工频fWaveData=[" + fWaveData + "]");
		int fDataLen = pointArr.size();
		int fSampRate = wave.getGp_take_fraq();// 采样率
		String fResultStr = "";
		WSClient.commitDTUWave_GP(service, fDTUID, fWaveBatchID, fAheadTime, fWaveData, fDataLen, fSampRate,
				fResultStr);
		// 上传性质 工频不需要上传
		// int fRelaFlag = 1;
		// ret =
		// service.getWCFBindingServiceInterface().commitWaveRelaFlag(company.getaUserID(),
		// company.getaPWD(),
		// company.getaCompID(), fWaveBatchID, fRelaFlag, fResultStr);
		// logger.info("传行波故障性质(commitWaveRelaFlag)，fDTUID=[" + fDTUID +
		// "],fAheadTime=[" + fAheadTime + "],fWaveBatchID=["
		// + fWaveBatchID + "],返回结果[" + ret + "]");

	}

	private String getXbWaveBatchID(String dtuid, String fAheadTime) {
		return "xb_" + dtuid + "_" + fAheadTime;// 重组key，上传用
	}

	private String getGpWaveBatchID(String dtuid, String fAheadTime) {
		return "gp_" + dtuid + "_" + fAheadTime;// 重组key，上传用
	}

	private String list2Csv(List<Integer> pointArr) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < pointArr.size(); i++) {
			int point = pointArr.get(i);
			sb.append(point);
			if (i == pointArr.size() - 1)
				continue;
			sb.append(",");
		}
		return "" + sb;
	}

	private List<Integer> getXbPointArr(Connection conn, Wave wave) throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		for (int packno = 1; packno <= wave.getXb_pack_total(); packno++) {
			WaveDataXbDao xbdao = new WaveDataXbDao();
			WaveDataXb xb = xbdao.getWaveDataXb(conn, wave.getSerial(), packno);
			if (xb == null) {
				for (int i = 0; i < 256; i++) {
					list.add(0);
				}
				continue;
			}
			int[] dataArr = new int[0];
			try {
				dataArr = WaveData.csv2IntArr(xb.getWave_data());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			for (int i = 0; i < dataArr.length; i++)
				list.add(dataArr[i]);
		}
		return list;
	}

	private List<Integer> getGpPointArr(Connection conn, Wave wave) throws SQLException {
		List<Integer> list = new ArrayList<Integer>();
		for (int packno = 1; packno <= wave.getGp_pack_total(); packno++) {
			WaveDataGpDao gpdao = new WaveDataGpDao();
			WaveDataGp gp = gpdao.getWaveDataGp(conn, wave.getSerial(), packno);
			if (gp == null) {
				for (int i = 0; i < 256; i++) {
					list.add(0);
				}
				continue;
			}
			int[] dataArr = new int[0];
			try {
				dataArr = WaveData.csv2IntArr(gp.getWave_data());
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
			for (int i = 0; i < dataArr.length; i++)
				list.add(dataArr[i]);
		}
		return list;
	}

	private int getMaxSerial(List<Wave> waveList) {
		int max = 0;
		for (Wave wave : waveList) {
			max = wave.getSerial() > max ? wave.getSerial() : max;
		}
		return max;
	}

}
