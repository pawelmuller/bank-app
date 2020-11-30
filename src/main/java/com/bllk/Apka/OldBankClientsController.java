package com.bllk.Apka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import javax.validation.Valid;

/*

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/api")
public class OldBankClientsController {
    @Autowired
    private BankClientsRepository clientsRepository;

    @GetMapping("/clients")
    public ResponseEntity<List<BankClients>> getAllClients() {
        List<BankClients> clients = new ArrayList<BankClients>();
        clientsRepository.findAll().forEach(clients::add);
        if (clients.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(clients, HttpStatus.OK);
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<BankClients> getClientById(@PathVariable(value = "id") Long clientId) {
        Optional<BankClients> clientData = clientsRepository.findById(clientId);
        if (clientData.isPresent()) {
            return new ResponseEntity<>(clientData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/clients")
    public ResponseEntity<BankClients> createClient(@Valid @RequestBody BankClients client) {
        try {
            BankClients new_client = clientsRepository.save(new BankClients(client.getID(), client.getName(), client.getSurname()));
            return new ResponseEntity<>(new_client, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity<BankClients> updateClient(@PathVariable("id") long clientId,
        @Valid @RequestBody BankClients client) {
        Optional<BankClients> clientData = clientsRepository.findById(clientId);

        if (clientData.isPresent()) {
            BankClients new_client = clientData.get();
            new_client.setID(client.getID());
            new_client.setName(client.getName());
            new_client.setSurname(client.getSurname());
            return new ResponseEntity<>(clientsRepository.save(new_client), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/clients/{id}")
    public ResponseEntity<HttpStatus> deleteClient(@PathVariable(value = "id") Long clientId) {
        try {
            clientsRepository.deleteById(clientId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/clients")
    public ResponseEntity<HttpStatus> deleteAllClients() {
        try {
            clientsRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

 */