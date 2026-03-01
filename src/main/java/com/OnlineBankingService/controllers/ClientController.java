package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.CreateClientDto;
import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.services.ClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public List<Client> getAll() {
        return clientService.findAll();
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable UUID id) {
        return clientService.findById(id);
    }

    @PostMapping
    public Client create(@RequestHeader("Authorization") String authorization, @RequestBody CreateClientDto client) {
        return clientService.create(client, authorization);
    }

    @PutMapping("/{id}")
    public Client update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Client client) {
        return clientService.update(id, client, authorization);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID id) {
        clientService.delete(id, authorization);
    }

    @PatchMapping("/{id}/lock")
    public Client lock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return clientService.lock(id, token);
    }

    @PatchMapping("/{id}/unlock")
    public Client unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return clientService.unlock(id,token);
    }
}
