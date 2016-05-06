package com.sf.hx.data;

import java.util.List;

public interface IDBService {

	/*
	 * Save Object
	 */
	void save(Object obj);

	/*
	 * update Object
	 */
	void update(Object obj);

	/*
	 * Get Records where ID are in the primary Key Column supplied
	 */

	List<?> getRecordsWhereIDsInPrimaryKeyColumn(List<Long> ids, String primaryKeyColumn, String clazz);

}