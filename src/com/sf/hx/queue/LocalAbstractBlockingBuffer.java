package com.sf.hx.queue;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;

public abstract class LocalAbstractBlockingBuffer<T> {
	private ConcurrentLinkedQueue<T> localQueue;
	private Map<String, Integer> keys;
	Logger logger = Logger.getLogger(getClass());

	private int queueSize = 0;
	private int capacity;

	public LocalAbstractBlockingBuffer() {
		this(2000);
	}

	/**
	 * Constructs a new Blocking Buffer
	 * 
	 * @param capacity
	 *            max number of items that can be contained in the queue
	 */
	public LocalAbstractBlockingBuffer(int capacity) {
		this.capacity = capacity + capacity / 4;
		localQueue = new ConcurrentLinkedQueue<>();
		this.keys = new HashMap<>();
	}

	/**
	 *
	 * @param item
	 * @return true if added, false otherwise
	 */

	public boolean put(T item) {

		if (contains(item)) {
			return false;
		}
		boolean offered = localQueue.offer(item);
		if (offered) {
			keys.put(String.valueOf(item.hashCode()), Integer.valueOf(1));
			// Edited, I put the Key as the hashCode from id that it used to be
			// before

			queueSize += 1;
		}
		logger.debug("Queue Size: " + queueSize);
		return offered;
	}

	/**
	 * Retrieves and then removes edited by jaohar
	 * 
	 * @return SyncItem or null if queue is empty
	 */

	public T get() {
		T item = null;
		item = localQueue.poll();
		if (item != null) {
			keys.remove(item);
			queueSize -= 1;
		}

		return item;
	}

	public boolean isFull() {
		return queueSize >= capacity;
	}

	public boolean contains(T item) {
		return keys.get(item) != null;
	}

}
