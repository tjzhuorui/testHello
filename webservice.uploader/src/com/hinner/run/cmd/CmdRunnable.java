package com.hinner.run.cmd;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.hinner.api.WSClient;
import com.hinner.ws.api.Service;

/**
 * 线程2 下发指令
 * 
 * @author Administrator
 *
 */
public class CmdRunnable implements Runnable {
	public static Logger logger = Logger.getLogger(CmdRunnable.class);

	public CmdRunnable() {
		super();
	}

	@Override
	public void run() {
		try {
			Service service = WSClient.newService();
			// 获取下发指令
			logger.info("查询下发指令");
			String resp = WSClient.getSendCom(service);
			logger.info("解析下发指令");
			List<Cmd> cmdlist = parseCmdStr(resp);

			logger.info("查询到[" + cmdlist.size() + "]条[下发]命令");
			for (Cmd cmd : cmdlist) {// 实际项目中，并不把参数真正的发送给我们的设备，后台有指令，我们直接更改其指令即可
				String fResultStr = "";
				WSClient.setSendCom(service, cmd.getSerial(), 1, fResultStr);
			}
			logger.info("[" + cmdlist.size() + "]条[下发]命令处理完毕");
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private static List<Cmd> parseCmdStr(String cmdStr) {
		List<Cmd> list = new ArrayList<Cmd>();
		String[] ss = cmdStr.split("\n");
		for (int i = 1; i < ss.length; i++) {
			// 流水号 厂家编号 设备编号 命令发送时间 帧类型 报文类型 命令个数 命令内容
			Cmd cmd = new Cmd();
			String[] params = ss[i].trim().split("\t");
			cmd.setSerial(Integer.parseInt(params[0]));
			cmd.setDtuid(params[2]);
			cmd.setFrameType(Byte.parseByte(params[4]));
			cmd.setMsgType(Byte.parseByte(params[5]));
			cmd.setParamNum(Integer.parseInt(params[6]));
			try {
				cmd.setParamStr(params[7]);
			} catch (ArrayIndexOutOfBoundsException ae) {

			}
			logger.info("获取控制命令:" + cmd);
			list.add(cmd);
		}
		return list;
	}

}
