package com.sf.hx.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.sf.hx.cache.MemCacheClient;
import com.sf.hx.master.GenericLoader;
import com.sf.hx.master.Loader;
import com.sf.hx.property.AppProperties;
import com.sf.hx.queue.LocalBaseEntityBlockingBuffer;
import com.sf.hx.slave.GenericWorker;
import com.sf.hx.slave.Worker;

public abstract class Main {
	Logger logger = Logger.getLogger(Main.class);

	private static Integer numberOfWorkerThreads = Integer.valueOf(1);
	private static String hexDirectoryForBackup;
	private static String hexDirectoryForBackupOfNewByte;
	private static String filepath;
	private static String fileContainingLastIdRead;
	private static boolean backUpHexToFileBeforeConverting = true;
	private static boolean storeHexToFileAfterConverting = true;
	private static String fullClassName;
	private static String primaryKeyColumn;

	public static void main(String[] args) throws IOException {

		if (args == null || args.length <= 0) {
			Logger.getLogger(Main.class).info("Please Launch Using Console.");
			// throw new RuntimeException("Application should be run from
			// Console.");
			// System.exit(1);
			// return;
		}

		Logger.getLogger(Main.class).info("Application Started");

		loadPropertyFile();
		process();
	}

	@SuppressWarnings("boxing")
	public static void loadPropertyFile() {
		Properties prop = AppProperties.getProperties();
		numberOfWorkerThreads = Integer.valueOf(prop.getProperty("numberOfWorkerThreads"));
		hexDirectoryForBackup = prop.getProperty("hexDirectoryForBackup");
		filepath = prop.getProperty("filepath");
		fileContainingLastIdRead = prop.getProperty("fileContainingLastIdRead");
		primaryKeyColumn = prop.getProperty("primaryKeyColumn");
		fullClassName = prop.getProperty("fullClassName");
		backUpHexToFileBeforeConverting = Boolean.valueOf(prop.getProperty("backUpHexToFileBeforeConverting"));
		storeHexToFileAfterConverting = Boolean.valueOf(prop.getProperty("storeHexToFileAfterConverting"));
		hexDirectoryForBackupOfNewByte = prop.getProperty("hexDirectoryForBackupOfNewByte");

	}

	@SuppressWarnings("boxing")
	public static void process() {

		try {

			MemCacheClient memCacheClient = MemCacheClient.getInstance();

			LocalBaseEntityBlockingBuffer<Object> leBuffer = new LocalBaseEntityBlockingBuffer<Object>();

			Loader<?> loader = new GenericLoader(leBuffer, memCacheClient, filepath, fileContainingLastIdRead,
					fullClassName, primaryKeyColumn);

			Worker<?> worker = new GenericWorker(leBuffer, hexDirectoryForBackup, backUpHexToFileBeforeConverting,
					storeHexToFileAfterConverting, hexDirectoryForBackupOfNewByte);

			Thread loaderThread = new Thread(loader);
			loaderThread.start();

			ScheduledExecutorService s = new ScheduledThreadPoolExecutor(numberOfWorkerThreads);
			s.schedule(worker, 6, TimeUnit.SECONDS);

			return;

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("resource")
	public static void test() throws FileNotFoundException, UnsupportedEncodingException {
		// Test

		int counter = 180504;
		PrintWriter writer = null;
		try {
			writer = new PrintWriter("C:\\hexRecords\\file.txt", "UTF-8");

			for (int j = 0; j < 1300; j++) {
				++counter;

				writer.println(counter);

			}

		} catch (Exception e) {

		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		// for (int i = 0; i < 1000; i++)
		// new FixHexIssues().persistSampleDataToDB();
	}

	@SuppressWarnings("unused")
	private static String getMethod(String prefix, String field) {
		String methodSuffix = makeFirstLetterCapital(field.trim());

		String getterMethodName = prefix + methodSuffix;

		return getterMethodName;
	}

	private static String makeFirstLetterCapital(String str) {
		if ((str == null) || (str.length() == 0)) {
			return str;
		}
		return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
	}

}
