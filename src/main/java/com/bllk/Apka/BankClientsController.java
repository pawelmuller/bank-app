package com.bllk.Apka;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import javax.validation.Valid;


import com.bllk.Apka.ResourceNotFoundException;
import com.bllk.Apka.BankClients;
import com.bllk.Apka.BankClientsRepository;

@RestController
@RequestMapping("/api/v1")
public class BankClientsController {
    @Autowired
    private BankClientsRepository clientsRepository;

    @GetMapping("/clients")
    public Iterable<BankClients> getAllClients() {
        return clientsRepository.findAll();
    }

    @GetMapping("/clients/{id}")
    public ResponseEntity<BankClients> getClientById(@PathVariable(value = "id") Long clientId)
            throws ResourceNotFoundException {
        BankClients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found for this id :: " + clientId));
        return ResponseEntity.ok().body(client);
    }

    @PostMapping("/clients")
    public BankClients createClient(@Valid @RequestBody BankClients client) {
        return clientsRepository.save(client);
    }

    @PutMapping("/clients/{id}")
    public ResponseEntity < BankClients > updateClient(@PathVariable(value = "id") Long clientId,
        @Valid @RequestBody BankClients clientDetails) throws ResourceNotFoundException {
        BankClients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + clientId));

        client.setName(clientDetails.getName());
        client.setSurname(clientDetails.getSurname());
        final BankClients updatedClient = clientsRepository.save(client);
        return ResponseEntity.ok(updatedClient);
    }

    @DeleteMapping("/clients/{id}")
    public Map< String, Boolean > deleteClient(@PathVariable(value = "id") Long clientId)
            throws ResourceNotFoundException {
        BankClients client = clientsRepository.findById(clientId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found for this id :: " + clientId));

        clientsRepository.delete(client);
        Map < String, Boolean > response = new HashMap< >();
        response.put("deleted", Boolean.TRUE);
        return response;
    }
}