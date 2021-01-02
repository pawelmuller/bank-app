package com.bllk.Servlet;
import com.bllk.Servlet.mapclasses.*;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.procedure.ProcedureCall;
import org.hibernate.procedure.ProcedureOutputs;
import org.hibernate.query.Query;
import org.hibernate.result.Output;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class Database {
    private static SessionFactory factory;

    public Database() {
        refresh();
    }
    private void refresh() {
        try {
            Configuration configuration = new Configuration();

            Properties settings = new Properties();
            settings.put(Environment.DRIVER, "oracle.jdbc.OracleDriver");
            settings.put(Environment.URL, "***REMOVED***");
            settings.put(Environment.USER, "***REMOVED***");
            settings.put(Environment.PASS, "***REMOVED***");
            settings.put(Environment.DIALECT, "org.hibernate.dialect.Oracle8iDialect");
            settings.put(Environment.SHOW_SQL, "true");
            configuration.setProperties(settings);

            configuration.addAnnotatedClass(Account.class);
            configuration.addAnnotatedClass(Address.class);
            configuration.addAnnotatedClass(Client.class);
            configuration.addAnnotatedClass(Country.class);
            configuration.addAnnotatedClass(Currency.class);
            configuration.addAnnotatedClass(Login.class);
            configuration.addAnnotatedClass(Transaction.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            System.out.println("Hibernate Java Config serviceRegistry created");
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client get_client(int clientid) {
        Client client = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Client WHERE id=" + clientid;
            Query query = session.createQuery(hql);
            client = (Client) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return client;
    }
    public List get_countries() {
        List countries = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Country";
            Query query = session.createQuery(hql);
            countries = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return countries;
    }
    public List get_currencies() {
        List currencies = null;

        try {
            Session session = factory.openSession();

            Query query = session.createQuery("FROM Currency");
            currencies = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return currencies;
    }
    public Login get_login(String login, String password) {
        Login result = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Login WHERE login='" + login + "' AND passwordhash='" + password + "'";
            Query query = session.createQuery(hql);
            List list = query.list();

            if (list.size() >= 1)
                result = (Login) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return result;
    }
    public Account get_money(int accountid) {
        Account account = null;
        try {
            Session session = factory.openSession();

            String hql = "FROM Account WHERE accountid=" + accountid;
            Query query = session.createQuery(hql);

            if (query.list().size() >= 1)
                account = (Account) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return account;
    }
    public boolean check_client(int accountid) {
        boolean valid = false;

        try {
            Session session = factory.openSession();

            String hql = "FROM Client WHERE id=" + accountid;
            Query query = session.createQuery(hql);
            List list = query.list();

            if (list.size() == 1)
                valid = true;
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return valid;
    }
    public void make_transfer(int payerid, int targetid, int amount) {
        try {
            Session session = factory.openSession();
            org.hibernate.Transaction tx = session.beginTransaction();

            Query q = session.createQuery("from Account where accountid=" + payerid);
            Account result = (Account)q.list().get(0);
            Integer i = result.getValue();
            result.setValue(i-amount);
            session.update(result);

            q = session.createQuery("from Account where accountid=" + targetid);
            result = (Account)q.list().get(0);
            i = result.getValue();
            result.setValue(i+amount);
            session.update(result);

//            Transaction transaction = new org.bllk.Transaction((Integer)payerid, (Integer)targetid, "nowy", amount, 0);
//            session.save(transaction);

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public int add_login(String _login, String _password) {
        try {
            Session session = factory.openSession();
            org.hibernate.Transaction tx = session.beginTransaction();

            int id = session.createSQLQuery("SELECT MAX(LOGIN_ID) FROM LOGINS").getFirstResult() + 1;
            Login login = new Login(id, _login, _password);
            session.save(login);

            tx.commit();
            session.close();
            return id;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
            return -1;
        }
    }
    public void add_client(String _name, String _surname, String _date, String _street,
                           String _num, String _city, String _postal_code,
                           String _country_name, String _login, String _password) {

        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

            StoredProcedureQuery query = session.createStoredProcedureQuery("ADD_CLIENT");
            query.registerStoredProcedureParameter("p_name",          String.class, ParameterMode.IN).setParameter("p_name",          _name        );
            query.registerStoredProcedureParameter("p_surname",       String.class, ParameterMode.IN).setParameter("p_surname",       _surname     );
            query.registerStoredProcedureParameter("p_birth_date",      Date.class, ParameterMode.IN).setParameter("p_birth_date",    formatter.parse(_date));
            query.registerStoredProcedureParameter("p_street",        String.class, ParameterMode.IN).setParameter("p_street",        _street      );
            query.registerStoredProcedureParameter("p_num",           String.class, ParameterMode.IN).setParameter("p_num",           _num         );
            query.registerStoredProcedureParameter("p_city",          String.class, ParameterMode.IN).setParameter("p_city",          _city        );
            query.registerStoredProcedureParameter("p_postal_code",   String.class, ParameterMode.IN).setParameter("p_postal_code",   _postal_code );
            query.registerStoredProcedureParameter("p_country_name",  String.class, ParameterMode.IN).setParameter("p_country_name",  _country_name);
            query.registerStoredProcedureParameter("p_login",         String.class, ParameterMode.IN).setParameter("p_login",         _login       );
            query.registerStoredProcedureParameter("p_password_hash", String.class, ParameterMode.IN).setParameter("p_password_hash", _password    );
            query.execute();

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
}