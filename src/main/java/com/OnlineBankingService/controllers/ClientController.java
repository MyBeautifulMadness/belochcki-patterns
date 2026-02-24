package com.OnlineBankingService.controllers;

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
    public Client create(@RequestBody Client client) {
        return clientService.create(client);
    }

    @PutMapping("/{id}")
    public Client update(@PathVariable UUID id, @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        clientService.delete(id);
    }

    @PatchMapping("/{id}/lock")
    public Client lock(@PathVariable UUID id) {
        return clientService.lock(id);
    }

    @PatchMapping("/{id}/unlock")
    public Client unlock(@PathVariable UUID id) {
        return clientService.unlock(id);
    }
}
