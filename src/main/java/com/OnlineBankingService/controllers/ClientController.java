package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.ClientClient;
import com.OnlineBankingService.dtos.CreateClientDto;
import com.OnlineBankingService.dtos.UpdateCreditRatingRequest;
import com.OnlineBankingService.entities.Client;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

    private final ClientClient clientClient;

    public ClientController(ClientClient clientClient) {
        this.clientClient = clientClient;
    }

    @GetMapping
    public List<Client> getAll() {
        return clientClient.getAll();
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable UUID id) {
        return clientClient.getById(id);
    }

    @PostMapping
    public Client create(@RequestHeader("Authorization") String authorization, @RequestBody CreateClientDto client) {
        return clientClient.create(authorization, client);
    }

    @PutMapping("/{id}")
    public Client update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Client client) {
        return clientClient.update(authorization,id, client);
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID id) {
        clientClient.delete(authorization, id);
    }

    @PutMapping("/{id}/lock")
    public Client lock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return clientClient.lock(id, token);
    }

    @PutMapping("/{id}/unlock")
    public Client unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return clientClient.unlock(id,token);
    }

    @GetMapping("/credit-rating")
    public Integer getCreditRating(@RequestParam UUID id) {
        return clientClient.getCreditRating(id);
    }

    @PutMapping("/credit-rating")
    public Integer updateCreditRating(@RequestBody UpdateCreditRatingRequest request) {
        return clientClient.updateCreditRating(
                request
        );
    }

    @GetMapping("/token")
    public Client getByToken(@RequestParam String token) {
        return clientClient.getByToken(token);
    }
}
