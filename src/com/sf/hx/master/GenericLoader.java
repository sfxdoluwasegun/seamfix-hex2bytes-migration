package com.sf.hx.master;

import java.util.List;

import com.sf.biocapture.entity.PassportData;
import com.sf.hx.cache.MemCacheClient;
import com.sf.hx.queue.LocalBaseEntityBlockingBuffer;

public class GenericLoader extends Loader<Object> {
	String clazz;
	String primaryKeyColumn;
	String filepath;
	String fileContainingLastIdRead;
	MemCacheClient cache;
	LocalBaseEntityBlockingBuffer<Object> queue;
	PassportData passport = new PassportData();

	public GenericLoader(LocalBaseEntityBlockingBuffer<Object> buffer, MemCacheClient cache, String filepath,
			String fileContainingLastIdRead, String clazz, String primaryKeyColumn) {

		super(buffer, cache, filepath, fileContainingLastIdRead);
		this.clazz = clazz;
		this.primaryKeyColumn = primaryKeyColumn;
		this.cache = cache;
		this.queue = buffer;
		this.filepath = filepath;
		this.fileContainingLastIdRead = fileContainingLastIdRead;
	}

	@Override
	public Long getIdOfObject(Object item) {
		passport = (PassportData) item;
		return passport.getId();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Object> readFromDB(List<Long> ids) {
		return (List<Object>) dbService.getRecordsWhereIDsInPrimaryKeyColumn(ids, primaryKeyColumn, clazz);
	}

	@Override
	public List<Object> getListOfObjectsToProcess() {

		return null;
	}

}
