package com.hinner.run.tz;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.sqlite.tz.entity.Dtu;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.ws.api.Service;

public class TzDtu {

	public static Logger logger = Logger.getLogger(TzDtu.class);

	public static List<Dtu> getDtuListFromServer(Service service, MLine line) {

		String resp = WSClient.getDTUList(service, line.getMLineID());

		// 解析返回结果
		List<Dtu> list = new ArrayList<Dtu>();
		String[] ss = resp.split("\n");
		for (int i = 1; i < ss.length; i++) {
			try {
				logger.info("[" + i + "][" + ss[i] + "]");
				String[] params = ss[i].trim().split("\t");
				Dtu dtu = new Dtu();
				dtu.setDtuid(params[0]);
				dtu.setmLineID(params[1]);
				dtu.setElePoID(params[2]);
				dtu.setsLineID(Integer.parseInt(params[3]));
				//
				list.add(dtu);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			}
		}
		return list;
	}

}
