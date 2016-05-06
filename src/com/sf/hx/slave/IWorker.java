package com.sf.hx.slave;

public interface IWorker<T> extends Runnable {

	void process();

	byte[] getByteArray(T obj);

	Long getIdOfObject(T obj);

	T setByteArray(byte[] image, T obj);

	void updateFixedRecord(T obj);

	void handleMasterRecord(T masterRecord, String cacheKey);

}