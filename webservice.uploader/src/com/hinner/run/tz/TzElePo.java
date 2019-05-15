package com.hinner.run.tz;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.sqlite.tz.entity.Dtu;
import com.hinner.sqlite.tz.entity.ElePo;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.ws.api.Service;

/**
 * 杆塔的台账
 * 
 * @author robin
 *
 */
public class TzElePo {

	public static Logger logger = Logger.getLogger(TzElePo.class);

	/**
	 * 调用接口getElePoList 获取某条线路的杆塔信息，并且
	 * 
	 * @param service
	 * @param company
	 * @param line
	 * @return
	 */
	public static List<ElePo> getElePoListFromServer(Service service, MLine line) {
		String resp = WSClient.getElePoList(service, line.getMLineID());
		// 解析报文
		List<ElePo> list = new ArrayList<ElePo>();
		String[] ss = resp.split("\n");
		for (int i = 1; i < ss.length; i++) {
			try {
				logger.info("[" + i + "][" + ss[i] + "]");
				String[] params = ss[i].trim().split("\t");
				ElePo elePo = new ElePo();
				elePo.setaMLineID(params[0]);
				elePo.setElePoID(params[1]);
				elePo.setSerial(Integer.parseInt(params[2]));
				elePo.setLineLen(Double.parseDouble(params[3]));
				// 还需设置线路ID，厂商编号
				elePo.setaMLineID(line.getMLineID());
				// 累计档距,no，此处还不能计算雷击档距，有可能获的杆塔是无序的
				//
				list.add(elePo);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}

	/**
	 * 按serial排序
	 * 
	 * @param elePoList
	 * @return
	 */
	public static void sort(List<ElePo> elePoList) {
		Collections.sort(elePoList, new Comparator<ElePo>() {
			@Override
			public int compare(ElePo e1, ElePo e2) {
				return e1.getSerial() - e2.getSerial();
			}
		});
	}

	/**
	 * 计算累计档距
	 * 
	 * @param elePoList
	 * @return 这条线路的总长度
	 */
	public static double calcAccumulateDis(List<ElePo> elePoList) {
		double total = 0.0;
		for (ElePo e : elePoList) {
			total += e.getLineLen();
			e.setTotalLen(total);
		}
		return total;
	}

	/**
	 * 查询设备安装处所处杆塔的绝对距离(距离起始点的距离)
	 * 
	 * @param dtuList
	 * @param elePoList
	 */
	public static void queryDtuAccumulateDis(List<Dtu> dtuList, List<ElePo> elePoList) {
		for (Dtu dtu : dtuList) {
			String elePoID = dtu.getElePoID();// 设备的杆塔ID
			for (ElePo ele : elePoList) {
				if (elePoID.equals(ele.getElePoID())) {
					dtu.setElePo(ele);
				}
			}
		}
	}

}
