package com.bllk.Apka;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface BankClientsRepository extends CrudRepository<BankClients, Long> {
    List<BankClients> findByName(@Param("name") String name);
}