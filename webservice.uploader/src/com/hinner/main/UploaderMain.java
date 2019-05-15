package com.hinner.main;

import java.sql.SQLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.common.util.DeployUtil;
import com.hinner.run.cmd.CmdRunnable;
import com.hinner.run.tz.TzRunnable;
import com.hinner.run.uploader.AccidentRun;
import com.hinner.run.uploader.BaseInfoRun;
import com.hinner.run.uploader.HeartRun;
import com.hinner.run.uploader.WaveRun;
import com.hinner.run.uploader.WorkRun;
import com.hinner.test.MiddleTest;
import com.hinner.test.TzTest;
import com.hinner.ws.api.Service;

public class UploaderMain {
	public static Logger logger = Logger.getLogger(UploaderMain.class);

	public static void main(String[] args) {
		// WSClient
		logger.info("【初始化WSClient】");
		WSClient.init();
		logger.info("【初始化WSClient】成功");

		logger.info("【开机测试】开始");
		// 开机测试以及初始化
		if (false == test()) {
			logger.error("开启测试未通过，系统即将退出.");
			System.exit(0);
			return;
		}
		logger.info("【开机测试】成功");

		// 先手动获取一次台账信息
		TzRunnable tzRunner = new TzRunnable();
		logger.info("【台账线程】试运行");
		tzRunner.run();
		int tzScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.tz.period.minute", "1440"));// 默认1天
		logger.info("【台账线程】执行周期.runnable.tz.period.minute=[" + tzScanPeriod + "]分钟");
		ScheduledExecutorService timer = Executors.newScheduledThreadPool(1);
		timer.scheduleAtFixedRate(tzRunner, tzScanPeriod, tzScanPeriod, TimeUnit.MINUTES);
		logger.info("【台账线程】成功启动");

		// 心跳线程
		HeartRun heartRunner = new HeartRun();
		logger.info("【心跳线程】试运行");
		heartRunner.run();
		int heartScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.heart.period.minute", "60"));// 默认1天
		logger.info("【心跳线程】执行周期.runnable.heart.period.minute=[" + heartScanPeriod + "]分钟");
		timer.scheduleAtFixedRate(heartRunner, heartScanPeriod, heartScanPeriod, TimeUnit.MINUTES);
		logger.info("【心跳线程】成功启动");

		// 工况线程
		WorkRun workRunner = new WorkRun();
		logger.info("【工况线程】试运行");
		workRunner.run();
		int workScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.work.period.minute", "1440"));// 默认1天
		logger.info("【工况线程】执行周期.runnable.work.period.minute=[" + workScanPeriod + "]分钟");
		timer.scheduleAtFixedRate(workRunner, workScanPeriod, workScanPeriod, TimeUnit.MINUTES);
		logger.info("【工况线程】成功启动");

		// 基本信息线程
		BaseInfoRun baseinfoRunner = new BaseInfoRun();
		logger.info("【基本信息线程】试运行");
		baseinfoRunner.run();
		int baseinfoScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.baseinfo.period.second", "10"));// 默认10秒
		logger.info("【基本信息线程】执行周期.runnable.baseinfo.period.second=[" + baseinfoScanPeriod + "]");
		timer.scheduleAtFixedRate(baseinfoRunner, baseinfoScanPeriod, baseinfoScanPeriod, TimeUnit.SECONDS);
		logger.info("【基本信息线程】成功启动");

		// 波形线程
		WaveRun waveRunner = new WaveRun();
		logger.info("【波形线程】试运行");
		waveRunner.run();
		int waveScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.wave.period.second", "60"));// 默认60秒
		logger.info("【波形线程]】执行周期.runnable.wave.period.second=[" + waveScanPeriod + "]秒");
		timer.scheduleAtFixedRate(waveRunner, waveScanPeriod, waveScanPeriod, TimeUnit.SECONDS);
		logger.info("【波形线程]】成功启动");

		// 故障线程
		AccidentRun accRunner = new AccidentRun();
		logger.info("【故障线程】试运行");
		accRunner.run();
		int accScanPeriod = Integer.parseInt(DeployUtil.getProperties("runnable.accident.period.second", "60"));// 默认60秒
		logger.info("【故障线程】执行周期.runnable.accident.period.second=[" + accScanPeriod + "]秒");
		timer.scheduleAtFixedRate(waveRunner, accScanPeriod, accScanPeriod, TimeUnit.SECONDS);
		logger.info("【故障线程】成功启动");

		// cmd线程 扫面ws的下发指令，将指令存储到t_recvdata表中,并告知后台已经处理
		int period_cmd = Integer.parseInt(DeployUtil.getProperties("runnable.cmd.period.second", "10"));
		logger.info("【cmd扫描周期】.runnable.cmd.period.second=[" + period_cmd + "]秒");
		int startdelay_cmd = period_cmd;
		timer = Executors.newScheduledThreadPool(1);
		timer.scheduleAtFixedRate(new CmdRunnable(), startdelay_cmd * 1000l, period_cmd * 1000l, TimeUnit.MILLISECONDS);
		logger.info("【cmd扫描线程】已经启动");

		logger.info("【程序成功启动】!!!!!");
	}

	/**
	 * 测试失败返回false
	 * 
	 * @return
	 */
	private static boolean test() {
		boolean flag = true;// 计算某一项测试没有过，后面也要继续测试
		// 测试
		logger.info("开始测试连接middle数据库");
		MiddleTest middle = new MiddleTest();
		try {
			middle.testQueryHeart();
			logger.info("middle库连接，成功.");
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
			flag = false;// 测试不通过
			logger.error("middle库连接.失败!!!");
		}
		//
		logger.info("开始测试连接tz库");
		TzTest tz = new TzTest();
		try {
			tz.testQueryLine();
			logger.info("tz库连接，成功.");
		} catch (ClassNotFoundException | SQLException e) {
			logger.error(e.getMessage(), e);
			flag = false;// 测试不通过
			logger.info("tz库连接，失败!!!");
		}
		//
		logger.info("开始测试webservice");
		try {
			WSClient.getEcho(WSClient.newService());
			logger.info("webservie测试，成功.");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			flag = false;// 测试不通过
			logger.info("webservie测试，失败!!!");
		}
		return flag;
	}

}