package com.hinner.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
	static SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");
	static SimpleDateFormat fmt2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public static String getCurrDateFmtWS() {
		return fmt.format(new Date());
	}

	/**
	 * 将yyyy-mm-dd hh:mm:ss转成yyyymmddhhmmss
	 * 
	 * @param datatime
	 * @return
	 */
	public static String our2their(String datatime) {
		return datatime.replaceAll("-", "").replaceAll(" ", "").replaceAll(":", "");
	}

	public static String our2their(String datatime, int ms, int us, int ns) {
		String temp = our2their(datatime);
		temp += fill0left(ms, 3);
		temp += fill0left(us, 3);
		temp += fill0left(ns, 3);
		return temp;
	}

	public static String fill0left(int integ, int bit) {
		String s = "" + integ;
		int bit_before = s.length();
		for (int i = bit_before; i < bit; i++) {
			s = "0" + s;
		}
		return s;
	}

	public static String theirMinus(int calcDelaySecond) {
		long l = System.currentTimeMillis() - calcDelaySecond * 1000l;
		return fmt.format(new Date(l));
	}

	public static void main(String[] args) {
		System.out.println(theirMinus(60));
	}

	public static String getYesterdayDate() {
		return fmt2.format(System.currentTimeMillis() - 1l * 24 * 60 * 60 * 1000);
	}

	// public static String getYesterdayDateFmt2() {
	// return fmt2.format(System.currentTimeMillis() - 1l * 24 * 60 * 60 * 1000);
	// }
}
