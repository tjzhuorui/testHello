package com.hinner.run.uploader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.FileUtil;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.ws.api.Service;

import middle.db.operator.dao.entity.BaseInfo;
import middle.db.operator.dao.pstmt.result.BaseInfoDao;

/**
 * 基本信息报文
 * 
 * @author robin
 *
 */
public class BaseInfoRun implements Runnable {
	public static Logger logger = Logger.getLogger(BaseInfoRun.class);

	public static final String PATH_SERIAL_BASEINFO = "configs/baseinfo.serial";

	public BaseInfoRun() {
		super();
	}

	@Override
	public void run() {
		long serial = FileUtil.readSerial(PATH_SERIAL_BASEINFO);
		logger.info("开始基本信息报文周期，serial=[" + serial + "]");
		// 查询需要上传的基本信息报文数据
		// 条件
		// 1.大于serial
		// 2.每个装置的最新数据
		List<BaseInfo> baseInfoList = getBaseInfoList(serial);
		if (baseInfoList == null || baseInfoList.size() == 0) {
			logger.info("本周期采集到的基本信息报文数据为空或者0");
			return;
		}
		// 更新本次查询的最大serial值
		serial = getMaxSerial(baseInfoList);
		FileUtil.saveSerial(PATH_SERIAL_BASEINFO, serial);
		logger.info("更新基本信息报文的serial=[" + serial + "]");

		// 上传基本信息报文数据
		Service service = WSClient.newService();
		logger.info("开始上传基本信息报文数据，总共有[" + baseInfoList.size() + "]基本信息报文数据需要进行上传");
		long t1 = System.currentTimeMillis();
		for (BaseInfo baseInfo : baseInfoList) {
			String fDTUID = baseInfo.getDtuid();
			String fDevName = baseInfo.getDtuname();
			String fDevModel = baseInfo.getDtutype();
			String fDevVersion = baseInfo.getDtuversion();
			// String fDevCompanyName = "HINNER";
			String fDevCompanyName = baseInfo.getManufacturer();
			String fProcudeDate = baseInfo.getManufacturetime();
			String fProcudeSerial = baseInfo.getManufactureserial();
			//
			String fResultStr = "";
			WSClient.submitDTUCore(service, fDTUID, fDevName, fDevModel, fDevVersion, fDevCompanyName, fProcudeDate,
					fProcudeSerial, fResultStr);
		}
		logger.info("基本信息报文上传完毕，总共传输了[" + baseInfoList.size() + "]条基本信息报文数据，共耗时["
				+ ((System.currentTimeMillis() - t1) / 1000) + "]秒");
	}

	private int getMaxSerial(List<BaseInfo> baseInfoList) {
		int max = 0;
		for (BaseInfo baseInfo : baseInfoList) {
			max = baseInfo.getSerial() > max ? baseInfo.getSerial() : max;
		}
		return max;
	}

	private List<BaseInfo> getBaseInfoList(long serial) {
		Connection conn = null;
		try {
			conn = JDBCConnUtil.getMiddleConn();
			//
			BaseInfoDao dao = new BaseInfoDao();
			return dao.getLatestGroupByDtuGtSerial(conn, serial);
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
		} finally {
			JDBCConnUtil.close(conn);
		}
		return null;
	}

}
