package com.sf.hx.master;

import java.util.List;

public interface ILoader<T> extends Runnable {

	/**
	 * reading file line by line in a batch of 1000 using BufferedReader Then
	 * Load into Queue and put Id in Cache
	 */
	public void process();

	public void handleLoading(List<Long> ids);

	public Long getIdOfObject(T item);

	public List<T> readFromDB(List<Long> ids);

}