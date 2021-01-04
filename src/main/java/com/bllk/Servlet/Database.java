package com.bllk.Servlet;
import com.bllk.Servlet.mapclasses.*;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.Transaction;

import javax.persistence.*;
import java.math.BigDecimal;
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
            configuration.addAnnotatedClass(Contact.class);
            configuration.addAnnotatedClass(Country.class);
            configuration.addAnnotatedClass(Credit.class);
            configuration.addAnnotatedClass(Currency.class);
            configuration.addAnnotatedClass(Investment.class);
            configuration.addAnnotatedClass(Login.class);
            configuration.addAnnotatedClass(TransactionRecord.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            factory = configuration.buildSessionFactory(serviceRegistry);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Client getClient(int loginid) {
        Client client = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Client WHERE login_id=" + loginid;
            Query query = session.createQuery(hql);
            List list = query.list();

            if (list.size() >= 1)
                client = (Client) query.list().get(0);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return client;
    }
    public String getSalt(String user_login) {
        String salt = null, hash = null;
        Login login = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Login WHERE login='" + user_login + "'";
            Query query = session.createQuery(hql);
            login = (Login) query.list().get(0);
            hash = login.getPasswordHash();
            salt = hash.substring(0, 29);

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return salt;
    }
    public List getCountries() {
        List countries = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Country ORDER BY name";
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
    public List getCurrencies() {
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
    public List getAccounts(String login, String hashed_password) {
        List accounts = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT A FROM Account A, Client C, Login L WHERE C.id=A.owner_id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashed_password);
            accounts = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return accounts;
    }
    public List getTransactionsIN(String login, String hashed_password) {
        List transactions = null;
        try {
            Session session = factory.openSession();
            Query query = session.createQuery("SELECT T FROM TransactionRecord T, Client C, Login L WHERE (C.id=T.receiverid OR C.id=T.senderid) AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2");
            query.setParameter("param", login);
            query.setParameter("param2", hashed_password);

            if (query.list().size() >= 1)
                transactions = query.list();

            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return transactions;
    }
    public Login getLogin(String login, String hashed_password) {
        Login result = null;

        try {
            Session session = factory.openSession();

            String hql = "FROM Login WHERE login='" + login + "' AND passwordhash='" + hashed_password + "'";
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
    public Account getAccount(int accountid) {
        Account account = null;
        try {
            Session session = factory.openSession();

            Query query = session.createQuery("SELECT A FROM Account A where A.id=:param");
            query.setParameter("param", accountid);

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
    public Account getAccount(String login, String hashed_password, int accountid) {
        Account account = null;
        try {
            Session session = factory.openSession();

            Query query = session.createQuery("SELECT A FROM Account A, Client C, Login L WHERE C.id=A.owner_id AND L.id = C.login_id AND L.login =:param AND L.passwordhash =:param2 AND A.id=:param3");
            query.setParameter("param", login);
            query.setParameter("param2", hashed_password);
            query.setParameter("param3", accountid);

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
    public boolean checkClient(int accountid) {
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
    public boolean checkAccount(int accountid) {
        boolean valid = false;
        try {
            Session session = factory.openSession();

            String hql = "FROM Account WHERE id=" + accountid;
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
    public Integer getTotalSavings(String login, String hashed_password, int currencyid) {
        Integer savings = null;
        try {
            Session session = factory.openSession();
            Query query = session.createSQLQuery("SELECT calculate_total_savings(CLIENT_ID, 'PLN') FROM CLIENTS C\n" +
                    "JOIN LOGINS L ON (L.LOGIN_ID=C.LOGIN_ID)\n" +
                    "WHERE L.LOGIN=:param AND L.PASSWORD_HASH=:param2;");
            query.setParameter("param", login);
            query.setParameter("param2", hashed_password);

            if (query.list().size() >= 1)
                savings = query.getFirstResult();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
        return savings;
    }
    public void makeTransfer(int payerid, int targetid, int amount, String title, int currencyid) {
        try {
            Session session = factory.openSession();
            Transaction tx = session.beginTransaction();

            Query q = session.createQuery("FROM Account WHERE id=:param");
            q.setParameter("param", payerid);
            Account payer = (Account)q.list().get(0);

            Integer i = payer.getValue();
            payer.setValue(i-amount);
            session.update(payer);

            q = session.createQuery("from Account where id=:param1");
            q.setParameter("param1", targetid);
            Account target = (Account) q.list().get(0);
            i = target.getValue();

            q = session.createSQLQuery("SELECT C.VALUE_IN_USD*(1/C2.VALUE_IN_USD) FROM CURRENCIES C, CURRENCIES C2 WHERE C.CURRENCY_ID=:param AND C2.CURRENCY_ID=:param2");
            q.setParameter("param", payer.getCurrencyID());
            q.setParameter("param2", target.getCurrencyID());
            float modifier = ((BigDecimal) q.list().get(0)).floatValue();
            int new_amount = (int) (amount*modifier);

            target.setValue(i+new_amount);
            session.update(target);

            int id = ((BigDecimal) session.createSQLQuery("SELECT MAX(TRANSACTION_ID) FROM TRANSACTIONS").list().get(0)).intValue() + 1;
            TransactionRecord transactionRecord = new TransactionRecord(id, payerid, targetid, title, amount, currencyid);
            session.save(transactionRecord);

            tx.commit();
            session.close();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            factory.close();
            refresh();
        }
    }
    public int addLogin(String _login, String _password) {
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
    public void addClient(String _name, String _surname, String _date, String _street,
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