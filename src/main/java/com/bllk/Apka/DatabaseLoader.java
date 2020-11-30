package com.bllk.Apka;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseLoader implements CommandLineRunner {
    private final BankClientsRepository clientsRepository;

    @Autowired
    public DatabaseLoader(BankClientsRepository repository) {
        this.clientsRepository = repository;
    }

    @Override
    public void run(String... strings) throws Exception {
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            SessionFactory factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
            Session session = factory.openSession();
            //---------------------------------------------------
            String hql = "FROM BankClients";
            Query query = session.createQuery(hql);
            List results = query.list();
            for (Object result : results) {
                this.clientsRepository.save((BankClients) result);
            }

            session.close();
            factory.close();
        } catch (Exception ex) {
            StandardServiceRegistryBuilder.destroy(registry);
        }
    }
}
