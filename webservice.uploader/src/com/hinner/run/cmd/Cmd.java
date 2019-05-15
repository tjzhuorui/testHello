package com.hinner.run.cmd;

import java.util.LinkedHashMap;
import java.util.Map;

public class Cmd {
	private int serial;// 流水号
	private String dtuid;
	private byte frameType;// 帧类型
	private byte msgType;// 报文类型
	private int paramNum;// 命令个数
	private String paramStr;// 命令内容

	public int getSerial() {
		return serial;
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public byte getFrameType() {
		return frameType;
	}

	public void setFrameType(byte frameType) {
		this.frameType = frameType;
	}

	public byte getMsgType() {
		return msgType;
	}

	public void setMsgType(byte msgType) {
		this.msgType = msgType;
	}

	public int getParamNum() {
		return paramNum;
	}

	public void setParamNum(int paramNum) {
		this.paramNum = paramNum;
	}

	public String getParamStr() {
		return paramStr;
	}

	public void setParamStr(String paramStr) {
		this.paramStr = paramStr;
	}

	public String getDtuid() {
		return dtuid;
	}

	public void setDtuid(String dtuid) {
		this.dtuid = dtuid;
	}

	@Override
	public String toString() {
		return "Cmd [serial=" + serial + ", dtuid=" + dtuid + ", frameType=" + frameType + ", msgType=" + msgType
				+ ", paramNum=" + paramNum + ", paramStr=" + paramStr + "]";
	}

	public Map<String, String> getParamMap() {
		Map<String, String> map = new LinkedHashMap<String, String>();
		String[] params = paramStr.split(";");
		for (int i = 0; i < params.length; i++) {
			String param = params[i];
			String[] ss = param.split("_");
			if (ss.length < 2)
				continue;
			if ("1".equals(ss[0])) {
				// 行波召回时间
				String key = "time_0001";
				String value = ss[1];
				map.put(key, value);
			} else if ("2".equals(ss[0])) {
				String key = "int_0002";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("3".equals(ss[0])) {
				String key = "int_0003";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("4".equals(ss[0])) {
				String key = "int_0004";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("5".equals(ss[0])) {
				// 工频召回时间
				String key = "time_0005";
				// String[] sss = ss[1].split(":");
				// String value = "00" + sss[0] + sss[1] + sss[2];
				String value = ss[1];
				map.put(key, value);
			} else if ("6".equals(ss[0])) {
				String key = "int_0006";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("7".equals(ss[0])) {
				String key = "int_0007";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("8".equals(ss[0])) {
				String key = "int_0008";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			} else if ("9".equals(ss[0])) {
				// 工况召回时间
				String key = "time_0009";
				// String[] sss = ss[1].split(":");
				// String value = "00" + sss[0] + sss[1] + sss[2];
				String value = ss[1];
				map.put(key, value);
			} else if ("10".equals(ss[0])) {
				String key = "int_000A";
				double d = Double.parseDouble(ss[1]);
				String value = "" + (int) d;
				map.put(key, value);
			}
		}
		return map;
	}

}
