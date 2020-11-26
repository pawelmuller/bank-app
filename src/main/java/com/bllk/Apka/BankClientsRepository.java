package com.bllk.Apka;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "clients", path = "clients")
public interface BankClientsRepository extends PagingAndSortingRepository<BankClients, Long> {
    List<BankClients> findByName(@Param("name") String name);
}