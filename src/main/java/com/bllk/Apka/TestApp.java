package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

public class TestApp {
    public static void main(String[] args) {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = factory.openSession();
            Transaction transaction = session.beginTransaction();

            BankClients client = new BankClients(1,"Tomasz", "Niewiemjaki");
            session.save(client);
            transaction.commit();

            String hql = "FROM BankClients";
            Query query = session.createQuery(hql);
            List results = query.list();

            for (int i=0; i<results.size(); i++) {
                BankClients reg = (BankClients) results.get(i);
                System.out.println(reg.getName());
                System.out.println(reg.getSurname());
            }

            session.close();
            factory.close();
        } catch (Exception ex) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
