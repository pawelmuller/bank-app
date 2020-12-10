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
	private StandardServiceRegistry registry;
	private SessionFactory factory;

	DatabaseConnection() {
		refresh();
	}
	private void refresh() {
		registry = new StandardServiceRegistryBuilder().configure("hibernate.cfg.xml").build();
		Metadata meta = new MetadataSources(registry).getMetadataBuilder().build();
		factory = meta.getSessionFactoryBuilder().build();
	}

	public Logins get_login(String login, String password) {
		Logins result = new Logins();

		try {
			Session session = factory.openSession();

			String hql = "FROM Logins WHERE login='" + login + "' AND password='" + password + "'";
			Query query = session.createQuery(hql);
			List list = query.list();

			if (list.size() != 1)
				throw new Exception("Invalid login");
			else
				result = (Logins) query.list().get(0);

			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return result;
	}
	public double get_money(int accountid) {
		double result = 0.0;

		try {
			Session session = factory.openSession();

			String hql = "FROM Money WHERE accountid=" + accountid;
			Query query = session.createQuery(hql);
			Money money = (Money) query.list().get(0);
			result = money.getMoneyonaccount();

			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return result;
	}
	public BankClients get_client(int clientid) {
		BankClients client = new BankClients();

		try {
			Session session = factory.openSession();

			String hql = "FROM BankClients WHERE id=" + clientid;
			Query query = session.createQuery(hql);
			client = (BankClients) query.list().get(0);

			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return client;
	}
	public TransactionHistory get_history(int accountid) {
		TransactionHistory history = new TransactionHistory();
		try {
			Session session = factory.openSession();

			Query query = session.createQuery("FROM BankClients WHERE id= :accountid");
			query.setParameter("accountid", accountid);
			history = (TransactionHistory) query.list();

			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return history;
	}

	public boolean check_client(int accountid) {
		boolean valid = false;

		try {
			Session session = factory.openSession();

			String hql = "FROM BankClients WHERE id=" + accountid;
			Query query = session.createQuery(hql);
			List list = query.list();

			if (list.size() == 1)
				valid = true;
			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return valid;
	}
	public void make_transfer(int payerid, int targetid, double amount) {
		try {
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
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
	}
	public int create_client(String name, String surname, String password) {
		int clientid = 0;
		try {
			Session session = factory.openSession();
			Transaction tx = session.beginTransaction();

			BankClients client = new BankClients(name, surname);
			session.save(client);
			clientid = client.getID();

			Money money = new Money(0.0, (Integer)clientid);
			session.save(money);

			Logins login = new Logins(Integer.toString(clientid+1000), password, clientid);
			session.save(login);

			tx.commit();
			session.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
			refresh();
		}
		return clientid;
	}
}