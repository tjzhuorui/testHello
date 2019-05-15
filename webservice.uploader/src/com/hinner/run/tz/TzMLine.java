package com.hinner.run.tz;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.ws.api.Service;

/**
 * 线路台账类
 * 
 * @author robin
 *
 */
public class TzMLine {

	public static Logger logger = Logger.getLogger(TzMLine.class);

	/**
	 * 获取线路厂家下线路信息
	 * 
	 * @param service
	 * @param company
	 * @param linename
	 *            根据输入的线路名称进行模糊匹配，查找线路信息，如果入参为空，则返回拥有权限的所有线路信息
	 * @return
	 */
	public static List<MLine> getMlineListFromServer(Service service, String linename) {
		// 查询该厂商拥有的所有权限的线路
		String resp = WSClient.getMLineList(service, linename);

		// 解析返回结果
		List<MLine> list = new ArrayList<MLine>();
		String[] ss = resp.split("\n");
		for (int i = 1; i < ss.length; i++) {
			try {
				logger.info("[" + i + "][" + ss[i] + "]");
				String[] params = ss[i].trim().split("\t");
				MLine line = new MLine();
				line.setMLineID(params[0]);
				line.setMLName(params[1]);
				line.setVolevel(params[2]);
				line.setStation1(params[3]);
				line.setStation2(params[4]);
				// 万一最后一行是空行呢，防止数组越界，成功之后，最后再add
				list.add(line);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}
}
