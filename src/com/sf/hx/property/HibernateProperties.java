package com.sf.hx.property;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;

public class HibernateProperties {

	private static Properties properties = new Properties();
	private static String propertiesFilename = "hibernate.properties";
	private static String comments = "Properties Used to configure hibernate features of the App by Jaohar";

	public HibernateProperties() {
		loadProperties();
	}

	public synchronized static Properties getProperties() {
		loadProperties();
		return properties;
	}

	@SuppressWarnings("resource")
	private static Properties loadProperties() {

		FileInputStream fileInputStream = null;

		try {
			fileInputStream = new FileInputStream(propertiesFilename);
			properties.load(fileInputStream);
		} catch (FileNotFoundException e) {
			updateProperties();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (Exception e2) {
				Logger.getLogger(AppProperties.class).error("Error while closing stream for reading properties file",
						e2);
			}
		}
		return properties;
	}

	private static void updateProperties() {
		@SuppressWarnings("resource")
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(propertiesFilename);
			properties.setProperty("hibernate.connection.url", "jdbc:postgresql://localhost:5432/hexDBLocal");
			properties.setProperty("hibernate.connection.username", "seamfix");
			properties.setProperty("hibernate.connection.password", "k0l0");
			properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
			properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
			properties.setProperty("hibernate.current_session_context_class",
					"org.hibernate.context.internal.ThreadLocalSessionContext");
			properties.setProperty("hbm2ddl.auto", "update");
			properties.setProperty("format_sql", "true");
			properties.setProperty("show_sql", "true");

			properties.store(fileOutputStream, comments);

			System.out.println("Properies Stored");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				Logger.getLogger(AppProperties.class)
						.error("Error while closing stream for updating/creating Hibernateproperties file", e);
			}
		}
	}

}
