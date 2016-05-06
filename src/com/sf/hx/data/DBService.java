package com.sf.hx.data;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

public abstract class DBService implements IDBService {

	Logger logger = Logger.getLogger(DBService.class);

	public SessionFactory sessionFactory = SessionFactoryService.getInstance().getSessionFactory();

	public Session getSession() {
		return sessionFactory.openSession();

	}

	public DBService() {
	}

	/*
	 * Save object
	 * 
	 * @see com.sf.hx.data.IDBService#save(java.lang.Object)
	 */
	@Override
	public void save(Object obj) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			session.save(obj);
			session.flush();
			transaction.commit();
			logger.debug("Object Saved... \n");
		} catch (Exception ex) {
			logger.error("Exception occurred while Saving to DB ", ex);
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				session.close();
			}

		}

	}

	public void saveOrUpdate(Object obj) {
		Session session = null;
		Transaction transaction = null;

		try {
			session = getSession();
			transaction = session.beginTransaction();
			session.saveOrUpdate(obj);
			session.flush();
			transaction.commit();
			logger.debug("Saved/Updated... \n");
		} catch (Exception ex) {
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {

			if (session != null) {
				session.close();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.sf.hx.data.IDBService#update(java.lang.Object)
	 */
	@Override
	public void update(Object obj) {
		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			session.update(obj);
			session.flush();
			transaction.commit();
			logger.debug("Object Updated...\n");
		} catch (Exception ex) {
			ex.printStackTrace();
			if (transaction != null) {
				transaction.rollback();
			}
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/**
	 * Get Records where IDs in PrimaryKeyColumn For Example Select * from
	 */
	@Override
	public List<?> getRecordsWhereIDsInPrimaryKeyColumn(List<Long> ids, String primaryKeyColumn, String clazz) {
		List<?> records = null;
		Session session = null;
		Transaction transaction = null;
		try {
			session = getSession();
			transaction = session.beginTransaction();
			Criteria criteria = session.createCriteria(Class.forName(clazz));
			criteria.add(Restrictions.in(primaryKeyColumn, ids));
			records = criteria.list();
			transaction.commit();

		} catch (Exception e) {
			if (transaction != null) {
				transaction.rollback();
			}
			logger.error("An Error Occurred while getting records " + ids, e);
		} finally {
			if (session != null) {
				session.close();
			}

		}
		return records;
	}

}
