package com.hinner.common.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.apache.log4j.Logger;

public class DeployUtil {

	public static Logger logger = Logger.getLogger(DeployUtil.class);

	public static final String Path = "configs/deploy.properties";

	private static Properties prop = null;

	public static String getProperties(String key, String defaultValue) {
		if (prop == null) {
			prop = new Properties();
			InputStreamReader reader = null;
			try {
				reader = new InputStreamReader(new FileInputStream(Path), "UTF-8");
				prop.load(reader);
			} catch (FileNotFoundException e) {
				logger.error(e.getMessage(), e);
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			} finally {
				if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
		return prop.getProperty(key, defaultValue);
	}

}
