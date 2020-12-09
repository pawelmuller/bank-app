package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.query.Query;

import java.util.List;
import java.util.Scanner;

public abstract class Database {
	public static boolean validate(String name, String surname) {
		boolean indata = false;

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM BankClients";
			Query query = session.createQuery(hql);
			List results = query.list();
			for (Object result : results) {
				BankClients client = (BankClients) result;
				if (client.getName().equals(name) && client.getSurname().equals(surname)) {
					indata = true;
					break;
				}
			}
			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return indata;
	}
}