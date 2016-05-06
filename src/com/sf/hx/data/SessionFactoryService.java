package com.sf.hx.data;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

final class SessionFactoryService {

	private static SessionFactoryService sessionFactoryService;
	private SessionFactory sessionFactory;

	private SessionFactoryService() {
		if (sessionFactory == null) {

			Properties properties = com.sf.hx.property.HibernateProperties.getProperties();

			Configuration conf = new Configuration().configure("hibernate.cfg.xml").addProperties(properties);

			StandardServiceRegistryBuilder serviceRegistryBuilder = new StandardServiceRegistryBuilder();
			serviceRegistryBuilder.applySettings(conf.getProperties());

			sessionFactory = conf.buildSessionFactory(serviceRegistryBuilder.build());
		}

	}

	public static SessionFactoryService getInstance() {
		if (sessionFactoryService == null) {
			synchronized (SessionFactoryService.class) {
				sessionFactoryService = new SessionFactoryService();
			}
		}
		return sessionFactoryService;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

}
