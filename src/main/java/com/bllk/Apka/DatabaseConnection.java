package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.query.Query;

import javax.xml.crypto.Data;
import java.util.List;
import java.util.Scanner;

public class DatabaseConnection {
	public boolean validate(String name, String surname) {
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
	public boolean validate_login(String login, String password) {
		boolean valid = false;
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM Logins";
			Query query = session.createQuery(hql);
			List results = query.list();
			for (Object result : results) {
				Logins result_login = (Logins) result;
				if (result_login.getLogin().equals(login) && result_login.getPassword().equals(password)) {
					valid = true;
					break;
				}
			}
			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return valid;
	}

	public Logins check_login(String login, String password) {
		Logins result = new Logins();

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM Logins WHERE login='" + login + "' AND password='" + password + "'";
			Query query = session.createQuery(hql);
			List list = query.list();

			if (list.size() != 1)
				throw new Exception("Invalid login");
			else
				result = (Logins) query.list().get(0);

			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return result;
	}
	public BankClients get_client(String login) {
		BankClients client = new BankClients();

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM BankClients WHERE name='" + login + "'";
			Query query = session.createQuery(hql);
			client = (BankClients) query.list().get(0);

			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return client;
	}
	public double get_money(int accountid) {
		double result = 0.0;

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM Money WHERE accountid=" + accountid;
			Query query = session.createQuery(hql);
			Money money = (Money) query.list().get(0);
			result = money.getMoneyonaccount();

			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return result;
	}
}