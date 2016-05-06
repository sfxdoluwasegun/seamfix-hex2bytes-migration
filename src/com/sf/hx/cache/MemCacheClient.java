package com.sf.hx.cache;

import java.io.IOException;
import java.util.logging.Logger;

import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.MemcachedClientBuilder;
import net.rubyeye.xmemcached.XMemcachedClientBuilder;
import net.rubyeye.xmemcached.utils.AddrUtil;
import nw.commons.NeemClazz;

public class MemCacheClient extends NeemClazz {

	private static MemCacheClient cache;
	private MemcachedClient client;
	Logger logger = Logger.getGlobal();

	private MemCacheClient() {
		try {
			initCache();
		} catch (IOException e) {
			logger.info("Exception while initializing cache: " + e.getMessage());
		}
	}

	public static MemCacheClient getInstance() {
		if (cache == null) {
			synchronized (MemCacheClient.class) {
				cache = new MemCacheClient();
			}
		}
		return cache;
	}

	public Object getItem(String key) {
		try {
			return client.get(key);
		} catch (Exception e) {
			logger.info("Exception while retrieving from cache: " + e.getMessage());
		}
		return null;
	}

	public boolean setItem(String key, int exp, Object item) {
		try {
			return client.set(key, exp, item);
		} catch (Exception e) {
			logger.info("Exception while putting into cache: " + e.getMessage());
		}
		return false;
	}

	public boolean removeItem(String key) {
		try {
			return client.delete(key);
		} catch (Exception e) {
			logger.info("Exception while removing from cache: " + e.getMessage());
		}
		return false;
	}

	private void initCache() throws IOException {
		String servers = appProps.getProperty("memcached-servers", "127.0.0.1:11211");
		MemcachedClientBuilder builder = new XMemcachedClientBuilder(AddrUtil.getAddresses(servers));
		client = builder.build();
	}

}
