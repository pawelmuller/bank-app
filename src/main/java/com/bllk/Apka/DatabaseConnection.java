package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.util.List;

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
	public boolean check_client(int accountid) {
		boolean valid = false;

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();

			String hql = "FROM BankClients WHERE id=" + accountid;
			Query query = session.createQuery(hql);
			List list = query.list();

			if (list.size() == 1)
				valid = true;
			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
		return valid;
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

	public void make_transfer(int payerid, int targetid, double amount) {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();

			Query q = session.createQuery("from Money where accountid=" + payerid);
			Money result = (Money)q.list().get(0);
			Double i = result.getMoneyonaccount();
			result.setMoneyonaccount(i-amount);
			session.update(result);

			q = session.createQuery("from Money where accountid=" + targetid);
			result = (Money)q.list().get(0);
			i = result.getMoneyonaccount();
			result.setMoneyonaccount(i+amount);
			session.update(result);

			TransactionHistory transaction = new TransactionHistory((Integer)payerid, (Integer)targetid, (Double)amount, "PLN");
			session.save(transaction);

			tx.commit();
			session.close();
			factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
}