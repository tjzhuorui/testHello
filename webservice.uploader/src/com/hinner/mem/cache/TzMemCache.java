package com.hinner.mem.cache;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;

import com.hinner.run.tz.TzTableCommon;
import com.hinner.sqlite.tz.entity.Dtu;
import com.hinner.sqlite.tz.entity.ElePo;
import com.hinner.sqlite.tz.entity.MLine;

/**
 * 台账缓存
 * 
 * @author robin
 *
 */
public class TzMemCache {
	public static Logger logger = Logger.getLogger(TzMemCache.class);
	// 各种缓存区
	private static Map<String, String> localUpdateTimeMap = new ConcurrentHashMap<String, String>();// 本地表更新时间。(key,CompID:表代码)；value，更新时间
	private static Map<String, MLine> lineMap = new ConcurrentHashMap<String, MLine>();// key为线路ID，value为线路对象
	private static Map<String, Dtu> dtuMap = new ConcurrentHashMap<String, Dtu>();// key为dtuid
	private static Map<String, List<ElePo>> elePoMap = new ConcurrentHashMap<String, List<ElePo>>();// key为线路ID

	/**
	 * 获取厂商company下的aTableID的表的本地更新时间
	 * 
	 * @param company
	 * @param aTableID
	 * @return
	 */
	public static String getLocalUpdateTime(int aTableID) {
		String key = getLocalUpdateTimeMapKey(aTableID);
		return localUpdateTimeMap.get(key);
	}

	/**
	 * 覆盖厂商company下的aTableID的表的本地更新时间
	 * 
	 * @param company
	 * @param aTableID
	 * @param serverUpdateTime
	 */
	public static void setLocalUpdateTime(int aTableID, String serverUpdateTime) {
		String key = getLocalUpdateTimeMapKey(aTableID);
		localUpdateTimeMap.put(key, serverUpdateTime);
	}

	/**
	 * 组装本地缓存更新时间的map的key，以保证key的统一性
	 * 
	 * @param company
	 * @param aTableID
	 * @return
	 */
	private static String getLocalUpdateTimeMapKey(int aTableID) {
		return "aTableID:" + aTableID;
	}

	/**
	 * 更新各表的本地更新时间
	 * 
	 * @param company
	 * @param lineLocalUpdateTime
	 * @param elePoLocalUpdateTime
	 * @param dtuLocalUpdateTime
	 */
	public static void updateLocalTime(String lineServerUpdateTime, String elePoServerUpdateTime,
			String dtuServerUpdateTime) {
		TzMemCache.setLocalUpdateTime(TzTableCommon.TABLE_MLine, lineServerUpdateTime);
		TzMemCache.setLocalUpdateTime(TzTableCommon.TABLE_ElePo, elePoServerUpdateTime);
		TzMemCache.setLocalUpdateTime(TzTableCommon.TABLE_DTU, dtuServerUpdateTime);
	}

	/**
	 * 更新线路台账
	 * 
	 * @param lineid
	 * @param line
	 */
	public static void updateLine(String lineid, MLine line) {
		lineMap.put(lineid, line);
	}

	public static MLine getLine(String lineid) {
		return lineMap.get(lineid);
	}

	/**
	 * 将线路，该线路下的杆塔信息，该线路下的dtuList更新到缓存
	 * 
	 * @param company
	 * @param line
	 *            线路信息
	 * @param elePoList
	 *            杆塔列表，排过序的
	 * @param dtuList
	 *            设备列表
	 */
	public static void updateOneLine2Cache(MLine line, List<ElePo> elePoList, List<Dtu> dtuList) {
		// 线路
		lineMap.put(line.getMLineID(), line);
		// 装置
		for (Dtu dtu : dtuList) {
			dtuMap.put(dtu.getDtuid(), dtu);
		}
		// 杆塔
		elePoMap.put(line.getMLineID(), elePoList);
	}

	/**
	 * 通过dtuid，查找dtu
	 * 
	 * @param dtuid
	 * @return
	 */
	public static Dtu queryDtu(String dtuid) {
		return dtuMap.get(dtuid);
	}

	/**
	 * 通过dtuid，查找其所属的线路
	 * 
	 * @param dtuid
	 * @return
	 */
	public static MLine queytLine(String dtuid) {
		Dtu dtu = dtuMap.get(dtuid);
		if (dtu == null)
			return null;
		return lineMap.get(dtu.getmLineID());
	}

	/**
	 * 通过线路ID，查询这边线路下的杆塔信息，排序后的杆塔
	 * 
	 * @param mLineID
	 * @return
	 */
	public static List<ElePo> queryElePo(String mLineID) {
		return elePoMap.get(mLineID);
	}

}
