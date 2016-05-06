package com.sf.hx.slave;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.sf.hx.cache.MemCacheClient;
import com.sf.hx.queue.LocalBaseEntityBlockingBuffer;
import com.sf.hx.util.GlobalState;

public abstract class Worker<T> implements IWorker<T> {

	private MemCacheClient cache = MemCacheClient.getInstance();
	private String hexDirectoryForBackup;
	private boolean backUpHexToFileBeforeConverting;
	private String hexDirectoryForBackupOfNewByte;
	private boolean storeHexToFileAfterConverting;
	LocalBaseEntityBlockingBuffer<T> buffer;
	Logger logger = Logger.getLogger(Worker.class);
	private String id = UUID.randomUUID().toString();

	public Worker(LocalBaseEntityBlockingBuffer<T> buffer, String hexDirectoryForBackup,
			boolean backUpHexToFileBeforeConverting, boolean storeHexToFileAfterConverting,
			String hexDirectoryForBackupOfNewByte) {
		this.backUpHexToFileBeforeConverting = backUpHexToFileBeforeConverting;
		this.storeHexToFileAfterConverting = storeHexToFileAfterConverting;
		this.hexDirectoryForBackup = hexDirectoryForBackup;
		this.hexDirectoryForBackupOfNewByte = hexDirectoryForBackupOfNewByte;
		this.buffer = buffer;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sf.hx.slave.IWorker#process()
	 */
	@Override
	public void process() {

		logger.debug("Worker process called");

		T masterRecord = buffer.get();

		logger.debug("in Worker, Object MasterRecord : " + masterRecord);

		String cacheKey;

		while (masterRecord != null) {
			logger.info("Queue is not null");
			cacheKey = "HexObject_" + masterRecord.hashCode();

			try {
				logger.debug("handling record : " + masterRecord.hashCode());
				long intTime = System.currentTimeMillis();

				// Convert and update DB from Hex
				handleMasterRecord(masterRecord, cacheKey);

				long finishTime = System.currentTimeMillis();
				logger.debug("Time it took to Completely process a record in miliseconds: "
						+ String.valueOf(finishTime - intTime));

			} catch (Exception e) {

				logger.error("error handling master record : " + masterRecord.hashCode(), e);
				updateCache(cacheKey, "Failed");
			}

			masterRecord = buffer.get();
		}

		logger.debug(" Worker exiting process because Queue is Empty, Hoping for next");
	}

	protected void updateCache(String cacheKey, String status) {

		cache.setItem(cacheKey, 86400, status);

	}

	private boolean writeHexToFile(T obj) {
		try {
			FileUtils.writeByteArrayToFile(new File(hexDirectoryForBackup + getIdOfObject(obj) + ".txt"),
					getByteArray(obj));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private boolean writeNewHexToFile(T obj) {
		try {
			FileUtils.writeByteArrayToFile(new File(hexDirectoryForBackupOfNewByte + getIdOfObject(obj) + ".jpg"),
					getByteArray(obj));
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public void handleMasterRecord(T masterRecord, String cacheKey) {
		long intTime = System.currentTimeMillis();
		byte[] byteImage = getByteArray(masterRecord);
		Long uniqueId = getIdOfObject(masterRecord);

		logger.debug("cacheKey : " + cacheKey + ", uniqueId : " + uniqueId);

		// the byte Image is null we flag an error
		if (byteImage == null) {
			logger.debug("Hex Image Data is null for record : " + getIdOfObject(masterRecord));
			return;
		}

		logger.debug("Hex Conversion Request for record with id :" + getIdOfObject(masterRecord));

		Object status = cache.getItem(cacheKey);

		logger.debug("status : " + status);

		// this should not happen cos the loader should have put it in cache but
		// well just in case
		if (status == null) {
			logger.debug("skipping record : " + getIdOfObject(masterRecord));
			return;
		}

		if (backUpHexToFileBeforeConverting) {
			boolean write = this.writeHexToFile(masterRecord);
			logger.debug("Save Hex to file for with id " + masterRecord.hashCode() + ", Result is: " + write);
		}

		logger.debug("so we proceed with the conversion of record  : " + masterRecord.hashCode());
		boolean success = this.convertAndUpdateRecord(masterRecord);

		logger.debug("success : " + success + "for record : " + masterRecord.hashCode());

		if (success) {
			updateCache(cacheKey, "Successful");
		} else {
			updateCache(cacheKey, "Failed");
		}
		long stopTime = System.currentTimeMillis();
		logger.debug("Time it took for Worker to Convert and Update Record with Id " + masterRecord.hashCode() + " is: "
				+ (String.valueOf(stopTime - intTime)));
	}

	/**
	 * Implementation to Convert Hex image format to the regular Byte Array
	 * 
	 * @param bytes
	 * @return
	 */
	public byte[] convertHexToByte(byte[] bytes) {

		try {
			return getCorrectEscapeBytes(bytes);
		} catch (Exception e) {
			logger.debug("An Error Occurred while converting", e);
			return null;
		}
	}

	public boolean convertAndUpdateRecord(T obj) {
		boolean result = false;
		byte[] newByte = this.convertHexToByte(getByteArray(obj));

		// if (!newByte.equals(passport.getPassportData())) {
		if (newByte != getByteArray(obj)) {
			obj = setByteArray(newByte, obj);
			this.updateFixedRecord(obj);

			result = true;
		}
		if (storeHexToFileAfterConverting) {
			boolean writeNew = this.writeNewHexToFile(obj);
			logger.debug("Save New Hex to file for  with id " + getIdOfObject(obj) + ", Result is: " + writeNew);
		}
		return result;
	}

	/**
	 * Implementation to do the Conversion from Hex to Byte[]
	 * 
	 * @param bytes
	 * @return
	 * @Author Ezewuzi Okafor, Jaohar Added the try and catch and made it return
	 *         null if
	 */
	private static byte[] getCorrectEscapeBytes(byte[] bytes) {

		byte[] returnBytes = null;

		try {
			String str = new String(bytes);

			if ((str.length() % 2) != 1) {
				throw new IllegalArgumentException("Invalid byte array argument. The length should be odd");
			}

			str = str.substring(1);

			returnBytes = new byte[1 + (str.length() / 2)];

			int y = -1;

			returnBytes[0] = (byte) y;

			for (int x = 0; x < str.length(); x = x + 2) {
				String hexPair = str.charAt(x) + "" + str.charAt(x + 1);
				int someVal = Integer.parseInt(hexPair, 16);
				returnBytes[1 + (x / 2)] = (byte) someVal;
			}
		} catch (Exception e) {
			returnBytes = bytes;
			Logger.getLogger(Worker.class).error("Could not convert Byte, Byte already in the right format ");
		}

		return returnBytes;
	}

	@Override
	public void run() {
		try {
			logger.debug("Just Entered the Worker Thread : " + id);
			while (true) {

				for (int i = 0; i < 3; i++) {
					this.process();
					Thread.sleep(2000);
				}

				GlobalState globalState = GlobalState.getInstance();

				if (globalState.isTerminateProcess()) {
					break;
				}

			}

			logger.info("exiting worker : " + id);

			// System.out.println("End of Master Thread");
		} catch (InterruptedException v) {
			logger.debug("An Error Occurred in the Interrupt ", v);
		} catch (Exception ex) {
			logger.debug("An Error Occurred in Worker Thread", ex);
		}

	}

}
