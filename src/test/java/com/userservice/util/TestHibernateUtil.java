package com.userservice.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class TestHibernateUtil {

    private static final Logger logger = LogManager.getLogger(TestHibernateUtil.class);

    private static SessionFactory sessionFactory;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            sessionFactory = buildSessionFactory();
        }
        return sessionFactory;
    }

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder()
                    .configure("test-hibernate.cfg.xml");

            String url = System.getProperty("hibernate.connection.url");
            String username = System.getProperty("hibernate.connection.username");
            String password = System.getProperty("hibernate.connection.password");

            if (url != null) builder.applySetting("hibernate.connection.url", url);
            if (username != null) builder.applySetting("hibernate.connection.username", username);
            if (password != null) builder.applySetting("hibernate.connection.password", password);

            final StandardServiceRegistry registry = builder.build();
            return new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch (Exception e) {
            logger.error("Initial SessionFactory creation failed.", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Test SessionFactory closed.");
            sessionFactory = null;
        }
    }
}