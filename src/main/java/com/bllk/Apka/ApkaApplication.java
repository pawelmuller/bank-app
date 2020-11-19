package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.*;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import org.hibernate.query.Query;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class ApkaApplication {
	public static void main(String[] args) {
		SpringApplication.run(ApkaApplication.class, args);

		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure() // configures settings from hibernate.cfg.xml
				.build();
		try {
			SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
			Session session = factory.openSession();
			Transaction transaction = session.beginTransaction();
			//---------------------------------------------------

			Scanner myObj = new Scanner(System.in);  // Create a Scanner object
			System.out.println("Enter name");
			String name = myObj.nextLine();
			System.out.println("Enter surname");
			String surname = myObj.nextLine();

			String hql = "FROM BankClients";
			Query query = session.createQuery(hql);
			List results = query.list();
			boolean indata = false;
			for (int i=0; i<results.size(); i++) {
				BankClients client = (BankClients) results.get(i);
				if (client.getName().equals(name) && client.getSurname().equals(surname)) {
					System.out.println("You are in database");
					indata = true;
					break;
				}
			}
			if (!indata) {
				System.out.println("You are not in database. Adding...");
				BankClients client = new BankClients(name, surname);
				session.save(client);
				transaction.commit();
			}

			query = session.createQuery(hql);
			results = query.list();
			for (int i=0; i<results.size(); i++) {
				BankClients reg = (BankClients) results.get(i);
				System.out.println(reg.getName() + " " + reg.getSurname());
			}

		//---------------------------------------------------
		session.close();
		factory.close();
		} catch (Exception ex) {
			StandardServiceRegistryBuilder.destroy(registry);
		}
	}
}