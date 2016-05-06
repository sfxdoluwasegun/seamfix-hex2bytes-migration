package com.sf.hx.property;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class AppProperties {
	private static Properties properties = new Properties();
	private static String propertiesFilename = "hexfixer.properties";
	private static String comments = "Properties Used to configure basic features of the App by Jaohar \n for the Class, Enter 1 for PassportData, 2 for WsqImage, 3 for SignatureData";

	public AppProperties() {
		loadProperties();
	}

	public synchronized static Properties getProperties() {
		loadProperties();
		return properties;
	}

	@SuppressWarnings("resource")
	private static Properties loadProperties() {

		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(propertiesFilename);
			properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			updateProperties();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}

			} catch (Exception e2) {
				Logger.getLogger(AppProperties.class)
						.error("Error while closing stream for reading App properties file", e2);
			}
		}
		return properties;
	}

	private static void updateProperties() {
		@SuppressWarnings("resource")
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(propertiesFilename);
			properties.setProperty("numberOfWorkerThreads", "1");
			properties.setProperty("hexDirectoryForBackup", "C:\\hexRecords\\backup\\");
			properties.setProperty("hexDirectoryForBackupOfNewByte", "C:\\hexRecords\\backup\\new\\");
			properties.setProperty("filepath", "C:\\hexRecords\\file.txt");
			properties.setProperty("fileContainingLastIdRead", "C:\\hexRecords\\processed.txt");
			properties.setProperty("backUpHexToFileBeforeConverting", "true");
			properties.setProperty("storeHexToFileAfterConverting", "true");
			properties.setProperty("primaryKeyColumn", "id");
			properties.setProperty("fullClassName", "com.sf.biocapture.entity.PassportData");
			/*
			 * properties.setProperty("byteArrayField", "passportData");
			 * properties.setProperty("fullClassName",
			 * "com.sf.biocapture.entity.PassportData");
			 */
			properties.setProperty("classToFix", "1");

			properties.store(fileOutputStream, comments);

			System.out.println("Properies Stored");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				Logger.getLogger(AppProperties.class)
						.error("Error while closing stream for updating/creating properties file", e);
			}
		}
	}
}
