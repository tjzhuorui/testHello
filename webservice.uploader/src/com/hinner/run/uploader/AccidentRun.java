package com.hinner.run.uploader;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.DateUtil;
import com.hinner.common.util.DeployUtil;
import com.hinner.common.util.FileUtil;
import com.hinner.common.util.JDBCConnUtil;
import com.hinner.mem.cache.TzMemCache;
import com.hinner.sqlite.tz.entity.Dtu;
import com.hinner.sqlite.tz.entity.ElePo;
import com.hinner.sqlite.tz.entity.MLine;
import com.hinner.ws.api.Service;
import com.hinner.ws.entity.Acci;

import hinner.calc.api.CalcCommonUtil;
import hinner.calc.api.DiaLoacater;
import middle.db.operator.dao.entity.Wave;
import middle.db.operator.dao.pstmt.result.WaveDao;

/**
 * 故障线程
 * 
 * @author robin
 *
 */
public class AccidentRun implements Runnable {

	public static Logger logger = Logger.getLogger(AccidentRun.class);

	public static final String PATH_SERIAL_ACCI_WAVE = "configs/accident_wave.serial";

	public AccidentRun() {
		super();
	}

	@Override
	public void run() {
		long serial = FileUtil.readSerial(PATH_SERIAL_ACCI_WAVE);
		logger.info("开始accident周期，serial=[" + serial + "]");

		Connection conn = null;
		try {
			conn = JDBCConnUtil.getMiddleConn();
			// 查询没有被处理的波形,且24小时内的数据
			WaveDao dao = new WaveDao();
			String begintime = DateUtil.getYesterdayDate();
			List<Wave> waveList = dao.getWaveListGtSerial(conn, serial, begintime);
			logger.info("查询1分钟前,且大于时间[" + begintime + "],大于序号[" + serial + "]的故障波形，共[" + waveList.size() + "]条波形");
			// 更新serial
			serial = getMaxSerial(waveList);
			FileUtil.saveSerial(PATH_SERIAL_ACCI_WAVE, serial);

			// 将查询到的波形进行归类->故障
			logger.info("对波形进行分类");
			Map<String, Acci> accMap = classifyAccident(waveList);
			logger.info("根据线路、相位、触发时间归类后，共有[" + accMap.size() + "]条故障");
			if (accMap == null || accMap.size() == 0) {
				logger.info("本周期没有故障需要进行计算");
				return;
			}
			// 对故障进行故障定位
			Service service = WSClient.newService();
			for (Map.Entry<String, Acci> e : accMap.entrySet()) {
				try {
					Acci acc = e.getValue();
					// 查询故障相关的波形
					if (diaAcc(acc)) {
						// 上传诊断结果
						upload(service, acc);
					}
				} catch (Exception e2) {
					logger.error(e2.getMessage(), e2);
				}
			}
		} catch (ClassNotFoundException | SQLException e1) {
			logger.error(e1.getMessage(), e1);
		} finally {
			JDBCConnUtil.close(conn);
		}

	}

	private void upload(Service service, Acci acc) {
		// 上传计算数据
		logger.info("开始提交自动诊断结果");
		// String preTowerName = TzRunnable.dtulist.get(0).getTowername();
		// String nextTowerName = TzRunnable.dtulist.get(1).getTowername();
		String fResultStr = "";
		String fAcciID = "" + acc.getfAcciID();
		String fAcciTime = acc.getfAcciTime();
		String fMLineID = acc.getfMLineID();
		String fElePoID = acc.getfElePoID();
		String fAheadElePoID = acc.getfAheadElePoID();
		String fEndElePoID = acc.getfEndElePoID();
		int fSLType = acc.getfSLType();
		int fLight = acc.getfLight();
		int fLightPro = acc.getfLightPro();
		int fGate = acc.getfGate();
		String fNaofFault = acc.getfNaofFault();
		//
		WSClient.autoDiaRes(service, fAcciID, fAcciTime, fMLineID, fElePoID, fAheadElePoID, fEndElePoID, fSLType,
				fLight, fLightPro, fGate, fNaofFault, fResultStr);
		// 提交手工诊断结果，目的提交故障位置
		String fErrDistance = acc.getfErrDistance();// 故障距离
		String fBaseElePo = acc.getfBaseElePo();// 基准杆塔
		String needCommit = DeployUtil.getProperties("accident.commit.dia.result", "false");
		if ("true".equals(needCommit)) {
			WSClient.manualDiaResWGS(service, fAcciID, fAcciTime, fMLineID, fElePoID, fAheadElePoID, fEndElePoID,
					fSLType, fLight, fLightPro, fGate, fNaofFault, fErrDistance, fBaseElePo, fResultStr);
		}
	}

	/**
	 * 
	 * @param acc
	 * @return 是否成功计算了
	 */
	private boolean diaAcc(Acci acc) {
		// 波速度
		double speed = Double.parseDouble(DeployUtil.getProperties("calc.speed", "297"));
		logger.info("使用speed[" + speed + "]m/us进行计算");
		// 线路全长
		MLine line = acc.getLine();
		double lineLen = line.getTotalLen();
		//
		List<Wave> waveList = acc.getWaveList();
		if (waveList == null || waveList.size() < 2) {
			logger.error("故障[" + acc.getfAcciID() + "]关联故障波形小于2条，不能进行计算.");
			return false;
		}
		Wave wave1 = waveList.get(0);
		Wave wave2 = waveList.get(1);
		long ns1 = CalcCommonUtil.parseNs(wave1.getTrigger_ms(), wave1.getTrigger_us(), wave1.getTrigger_ns());
		long ns2 = CalcCommonUtil.parseNs(wave2.getTrigger_ms(), wave2.getTrigger_us(), wave2.getTrigger_ns());
		Dtu dtu1 = TzMemCache.queryDtu(wave1.getDtuid());
		Dtu dtu2 = TzMemCache.queryDtu(wave2.getDtuid());
		double dis1 = dtu1.getAbsulateDis();
		double dis2 = dtu2.getAbsulateDis();
		// 计算故障距离起始点的距离
		double accDistance = DiaLoacater.clac(speed, lineLen, ns1, dis1, ns2, dis2);
		acc.setAbsuluteDis(accDistance);
		// 确定故障类型和故障距离位置
		return calc(accDistance, acc);
	}

	private boolean calc(double accDistance, Acci acc) {
		List<ElePo> elelist = acc.getElePoList();
		if (elelist == null || elelist.size() == 0) {
			// 没有杆塔
			logger.error("这条线路[" + acc.getfMLineID() + "]没有配置杆塔，无法进行计算");
			return false;
		}
		// 计算出故障位置离第一级杆塔的距离
		String towername = locate2Tower(accDistance, elelist);
		// 填入计算结果
		ElePo basePo = elelist.get(0);// 定位基准杆塔
		acc.setfErrDistance("" + ((int) (accDistance - basePo.getTotalLen())));
		acc.setfBaseElePo(basePo.getElePoID());// 基准杆塔
		acc.setfElePoID(towername);// 故障所属杆塔
		//
		List<Wave> waveList = acc.getWaveList();
		Wave wave1 = waveList.get(0);
		Wave wave2 = waveList.get(1);
		Dtu dtu1 = TzMemCache.queryDtu(wave1.getDtuid());
		Dtu dtu2 = TzMemCache.queryDtu(wave2.getDtuid());
		acc.setfAheadElePoID(dtu1.getElePoID());
		acc.setfEndElePoID(dtu2.getElePoID());
		// 判断故障类型
		int fLight = 2;// 默认为非雷击
		for (Wave wave : waveList) {// 1雷击，3，4大概代表反击和绕击吧
			if (1 == wave.getGp_trigger_type() || 3 == wave.getGp_trigger_type() || 4 == wave.getGp_trigger_type()) {
				fLight = 1;
				break;// 只要有一个判断为雷击，那么就是雷击
			}
		}
		acc.setfLight(fLight);// 是否为雷击
		acc.setfLightPro(3);// 3.无法判断绕击或反击
		// 是否跳闸判断
		acc.setfGate(2);// 默认未跳闸
		for (Wave wave : waveList) {
			if (wave.getGp_trigger_type() == 0) {// 有一台发生跳闸则判断为跳闸 我们的协议0代表跳闸，与webservice的协议相反
				acc.setfGate(1);
				break;
			}
		}
		return true;
	}

	/**
	 * 通过线路、相别、触发时间对故障进行归类
	 * 
	 * @param waveList
	 * @return
	 */
	private Map<String, Acci> classifyAccident(List<Wave> waveList) {
		Map<String, Acci> accmap = new HashMap<String, Acci>();
		for (Wave wave : waveList) {
			// 通过dtuid，查询线路编号、相位
			// 通过，线路ID、相别、触发时间 形成key
			String dtuid = wave.getDtuid();
			Dtu dtu = TzMemCache.queryDtu(dtuid);
			logger.info("查询[" + dtuid + "]的台账DTU=[" + dtu + "]");
			if (dtu == null) {
				logger.info("波形[" + wave.getSerial() + "][" + wave.getTrigger_time() + "],dtuid=[" + dtuid
						+ "]，没有对应的【设备台账】信息，将不参与计算.");
				continue;
			}
			MLine line = TzMemCache.queytLine(dtuid);
			logger.info("设备[" + dtuid + "]所属线路[" + line + "]");
			if (line == null) {
				logger.info("波形[" + wave.getSerial() + "][" + wave.getTrigger_time() + "],dtuid=[" + dtuid
						+ "]，没有对应的【线路台账】信息，将不参与计算.");
				continue;
			}
			List<ElePo> elePoList = TzMemCache.queryElePo(line.getMLineID());
			if (elePoList == null || elePoList.size() == 0) {
				logger.info("波形[" + wave.getSerial() + "][" + wave.getTrigger_time() + "],dtuid=[" + dtuid
						+ "]，没有对应的【杆塔台账】信息，将不参与计算.");
				continue;
			}
			String tiggertime = DateUtil.our2their(wave.getTrigger_time(), wave.getTrigger_ms(), wave.getTrigger_us(),
					wave.getTrigger_ns());
			String key = line.getMLineID() + "_" + dtu.getsLineID() + "_" + tiggertime;// 线路id，相别,触发时间
			logger.info("故障分类的时的key[" + key + "]");
			//
			Acci acc = accmap.get(key);
			if (acc == null) {
				acc = new Acci();
				accmap.put(key, acc);
				//
				acc.setfAcciID(key);
				acc.setfSLType(dtu.getsLineID());// 相别
				acc.setfAcciTime(tiggertime);// 触发时间
			}

			acc.addWave(wave);// 将波形添加到故障实体的list中
			acc.setElePoList(elePoList);
			acc.setLine(line);
		}

		return accmap;
	}

	private int getMaxSerial(List<Wave> waveList) {
		int max = 0;
		for (Wave wave : waveList) {
			if (wave.getSerial() > max)
				max = wave.getSerial();
		}
		return max;
	}

	private static String locate2Tower(double dis, List<ElePo> elePoList) {// 找前后杆塔
		ElePo pre = elePoList.get(0);
		if (pre.getTotalLen() > dis) {// 第一级杆塔之前
			return pre.getElePoID();
		}
		//
		ElePo next = elePoList.get(1);
		for (int i = 0; i < elePoList.size() - 1; i++) {
			pre = elePoList.get(i);
			next = elePoList.get(i + 1);
			if (dis >= pre.getTotalLen() && dis < next.getTotalLen()) {
				// 找到了
				double d1 = Math.abs(dis - pre.getTotalLen());
				double d2 = Math.abs(dis - next.getTotalLen());
				if (d1 < d2)
					return pre.getElePoID();
				return next.getElePoID();
			}
		}
		// 最后一级杆塔
		ElePo last = elePoList.get(elePoList.size() - 1);
		return last.getElePoID();
	}

}
