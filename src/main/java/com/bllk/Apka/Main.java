package com.bllk.Apka;

import org.hibernate.*;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

public class Main {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = factory.openSession();
            Transaction transaction = session.beginTransaction();

            Regions regions = new Regions("USA");

            session.save(regions);
            transaction.commit();
            session.close();
            factory.close();

        } catch (Exception ex) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }

}