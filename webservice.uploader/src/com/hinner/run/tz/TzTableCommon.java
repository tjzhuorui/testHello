package com.hinner.run.tz;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.ws.api.Service;

/**
 * 台账后台表公公工具
 * 
 * @author robin
 *
 */
public class TzTableCommon {

	public static Logger logger = Logger.getLogger(TzTableCommon.class);

	// 表之编码
	public static final int TABLE_EleOff = 1;// 监管单位表
	public static final int TABLE_MLine = 2;// 线路信息表
	public static final int TABLE_ElePo = 3;// 杆塔信息表
	public static final int TABLE_DTU = 4;// 监管终端表
	public static final int TABLE_EOEP = 5;// 夸局信息表
	//
	// /**
	// * 判断是否需要进行台账更新，如果需要，还要更新缓存TzMenCache中该厂商该表的更新时间
	// *
	// * @param service
	// * @param company
	// * @param aTableID
	// * @return
	// */
	// public static boolean tzTableNeedUpdate(Service service, int aTableID) {
	// logger.info("开始检查线路表的台账");
	// // 获取服务器本地的更新时间
	// String localtime = TzMemCache.getLocalUpdateTime(aTableID);
	// logger.info("本地的localtime=[" + localtime + "]");
	// // 获取服务器的更新时间
	// String serverUpdateTime = WSClient.getTableUpdateTime(service, aTableID);
	// logger.info("从服务器获取的serverUpdateTime=[" + serverUpdateTime + "]");
	// //
	// // if (serverUpdateTime == null || "".equals(serverUpdateTime)) {
	// // logger.error("从服务器获取更新时间失败");
	// // return true;
	// // }
	//
	// // 如果为null，则表示第一次启动
	// if (localtime == null) {
	// TzMemCache.setLocalUpdateTime(aTableID, serverUpdateTime);
	// logger.info("本地时间为null，初始化，需要更新此表台账");
	// return true;
	// }
	//
	// // 本地 与 服务器 比较:相同，则不需要更新；不相同，则需要更新
	// if (!localtime.equals(serverUpdateTime)) {
	// TzMemCache.setLocalUpdateTime(aTableID, serverUpdateTime);
	// logger.info("本地时间与服务器上更新时间不一致，需要更新此表台账");
	// return true;
	// }
	// // 不需要更新
	// logger.info("不需要更新此表的台账");
	// return false;
	//
	// }

	/**
	 * 通过webservice接口.调用getTableUpdateTime 获取某表的更新时间
	 * 
	 * @param service
	 * @param company
	 * @param aTableID
	 * @return
	 */
	public static String getServerUpdateTime(Service service, int aTableID) {
		return WSClient.getTableUpdateTime(service, aTableID);
	}

	/**
	 * 判断台账是否需要更新，线路、设备、杆塔只要有一个台账发生变化，那么则需要更新
	 * 
	 * @param lineServerUpdateTime
	 * @param lineLocalUpdateTime
	 * @param elePoServerUpdateTime
	 * @param elePoLocalUpdateTime
	 * @param dtuServerUpdateTime
	 * @param dtuLocalUpdateTime
	 * @return
	 */
	public static boolean needUpdate(String lineServerUpdateTime, String lineLocalUpdateTime,
			String elePoServerUpdateTime, String elePoLocalUpdateTime, String dtuServerUpdateTime,
			String dtuLocalUpdateTime) {
		if (lineLocalUpdateTime == null || elePoLocalUpdateTime == null || dtuLocalUpdateTime == null)
			return true;
		if (!lineLocalUpdateTime.equals(lineServerUpdateTime))
			return true;
		if (!elePoLocalUpdateTime.equals(elePoServerUpdateTime))
			return true;
		if (!dtuLocalUpdateTime.equals(dtuServerUpdateTime))
			return true;
		return false;
	}

}
