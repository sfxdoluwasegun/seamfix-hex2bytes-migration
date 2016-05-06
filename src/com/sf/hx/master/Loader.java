package com.sf.hx.master;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sf.hx.cache.MemCacheClient;
import com.sf.hx.data.GenericService;
import com.sf.hx.queue.LocalBaseEntityBlockingBuffer;
import com.sf.hx.util.GlobalState;

public abstract class Loader<T> implements ILoader<T> {

	String filepath;
	String fileContainingLastIdRead;
	MemCacheClient cache;
	LocalBaseEntityBlockingBuffer<T> queue;
	GenericService dbService = new GenericService();
	Logger logger = Logger.getLogger(Loader.class);

	public Loader(LocalBaseEntityBlockingBuffer<T> buffer, MemCacheClient cache, String filepath,
			String fileContainingLastIdRead) {
		this.cache = cache;
		this.queue = buffer;
		this.filepath = filepath;
		this.fileContainingLastIdRead = fileContainingLastIdRead;

	}

	/*
	 * Read IDs from File and Load in Batches The file is configurable
	 * 
	 * @see com.sf.hx.master.ILoader#process()
	 */

	public void process() {
		logger.debug("Loader process called");

		int lineCounter = 0;
		FileInputStream fileInputStream = null;
		BufferedReader reader = null;
		try {

			fileInputStream = new FileInputStream(filepath);
			reader = new BufferedReader(new InputStreamReader(fileInputStream));

			logger.info("Reading File line by line using BufferedReader");

			List<Long> ids = new ArrayList<Long>();
			String line;
			while ((line = reader.readLine()) != null) {
				lineCounter++;
				ids.add(Long.valueOf(line));
				if (lineCounter % 1000 == 0) {
					long intTime = System.currentTimeMillis();
					this.handleLoading(ids);
					long finishTime = System.currentTimeMillis();
					logger.debug("Time it took to read 1000 Ids from file in miliseconds: "
							+ String.valueOf(finishTime - intTime));
					ids.clear();
				}
			}
			this.handleLoading(ids);
			this.writeIdOfProcessedRecordsToFile(fileContainingLastIdRead, String.valueOf(lineCounter));

		} catch (FileNotFoundException ex) {
			logger.error("File not Found ", ex);
		} catch (IOException ex) {
			logger.error("An Error Occurred ", ex);

		} finally {
			try {
				if (fileInputStream != null) {
					reader.close();
					fileInputStream.close();
				}

			} catch (IOException ex) {
				logger.error("An Error Occurred", ex);
			}
		}

	}

	public boolean writeIdOfProcessedRecordsToFile(String filePath, String index) {
		try {
			FileUtils.writeStringToFile(new File(filePath), index);
			return true;
		} catch (Exception e) {
			logger.debug("An Error Occurred while writing index of processed record to file", e);
			return false;
		}

	}

	@Override
	public void run() {
		try {
			logger.debug("Just Entered the Loader Thread");

			this.process();

			GlobalState.getInstance().setTerminateProcess(true);

			Thread.sleep(1000);

			System.out.println("End of Loader Thread.");
		} catch (InterruptedException v) {
			logger.debug("An Error Occurred in the Interrupt ", v);
		} catch (Exception ex) {
			logger.debug("An Error Occurred in Loader Thread", ex);
		}

	}

	@SuppressWarnings({ "boxing" })
	public void handleLoading(List<Long> ids) {

		// Read from DB
		List<T> records = null;

		if (ids != null) {
			try {
				records = this.readFromDB(ids);
			} catch (Exception e) {

				logger.error("An Error Occurred reading from DB", e);
			}
		}
		if (records == null || records.isEmpty()) {

			logger.debug("No items returned from DB...");
			return;
		}

		logger.debug("Records from DB returned successful.");

		// Put in Queue and Put Id in Cache
		long intTime = System.currentTimeMillis();
		for (T masterRecord : records) {

			if (masterRecord != null) {

				logger.debug("Processing Object with hashCode " + getIdOfObject(masterRecord));

				Long id = Long.valueOf(masterRecord.hashCode());

				String cacheKey = "HexObject_" + id;

				logger.debug("cacheKey : " + cacheKey);

				Object recordStatus = cache.getItem(cacheKey);

				logger.debug("recordStatus : " + recordStatus);

				// already loaded or processed by another instance so skip
				if (recordStatus != null) {
					logger.debug("skipping master record id : " + id);
					continue;
				}

				logger.debug("master record on course for loading : " + id);
				boolean inserted = queue.put(masterRecord);
				logger.debug("inserted : " + inserted);
				if (inserted) {
					cache.setItem(cacheKey, 86400, masterRecord.hashCode());
				} else {
					logger.debug("nothing set in cache for record " + id);
				}
			}

		}
		long stopTime = System.currentTimeMillis();

		logger.debug("Time it took to load 1005 records in queue and cache " + String.valueOf(stopTime - intTime));

		logger.debug("finished loading");

		return;
	}

	public abstract List<Object> getListOfObjectsToProcess();

}
