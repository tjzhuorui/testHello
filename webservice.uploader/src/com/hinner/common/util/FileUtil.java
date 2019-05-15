package com.hinner.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.log4j.Logger;

public class FileUtil {

	public static Logger logger = Logger.getLogger(FileUtil.class);

	public static long readSerial(String filepath) {
		File file = new File(filepath);
		FileInputStream input = null;
		String str = "0";
		try {
			if (!file.exists())
				file.createNewFile();
			input = new FileInputStream(file);
			byte[] buffer = new byte[16];
			int len = input.read(buffer);
			if (len == -1) {// 说明文件之前不存在
				return 0;
			}
			str = new String(buffer, 0, len);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (input != null)
				try {
					input.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}
		return Long.parseLong(str);
	}

	public static void saveSerial(String filepath, long serial) {
		File file = new File(filepath);
		FileOutputStream output = null;
		try {
			if (!file.exists())
				file.createNewFile();
			output = new FileOutputStream(file);
			output.write(("" + serial).getBytes());
			output.flush();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (output != null)
				try {
					output.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
		}
	}

	public static void main(String[] args) throws IOException {
		String path = "configs/test.serial";
		long seiral = readSerial(path);
		System.out.println(seiral);
		seiral = 100;
		saveSerial(path, seiral);
		seiral = readSerial(path);
		System.out.println(seiral);
		System.out.println(Integer.MAX_VALUE);
	}
}
