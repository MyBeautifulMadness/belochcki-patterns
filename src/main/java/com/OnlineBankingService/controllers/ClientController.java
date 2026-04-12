package com.OnlineBankingService.controllers;

import com.OnlineBankingService.configs.ClientClient;
import com.OnlineBankingService.configs.RetryExecutor;
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
    private final RetryExecutor retryExecutor;

    public ClientController(ClientClient clientClient, RetryExecutor retryExecutor) {
        this.clientClient = clientClient;
        this.retryExecutor = retryExecutor;
    }

    @GetMapping
    public List<Client> getAll() {
        return retryExecutor.execute(clientClient::getAll, "gateway -> info get all clients");
    }

    @GetMapping("/{id}")
    public Client getById(@PathVariable UUID id) {
        return retryExecutor.execute(() -> clientClient.getById(id), "gateway -> info get client by id");
    }

    @PostMapping
    public Client create(@RequestHeader("Authorization") String authorization, @RequestBody CreateClientDto client) {
        return retryExecutor.execute(() -> clientClient.create(authorization, client), "gateway -> info create client");
    }

    @PutMapping("/{id}")
    public Client update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Client client) {
        return retryExecutor.execute(() -> clientClient.update(authorization, id, client), "gateway -> info update client");
    }

    @DeleteMapping("/{id}")
    public void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID id) {
        retryExecutor.executeVoid(() -> clientClient.delete(authorization, id), "gateway -> info delete client");
    }

    @PutMapping("/{id}/lock")
    public Client lock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return retryExecutor.execute(() -> clientClient.lock(id, token), "gateway -> info lock client");
    }

    @PutMapping("/{id}/unlock")
    public Client unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token) {
        return retryExecutor.execute(() -> clientClient.unlock(id, token), "gateway -> info unlock client");
    }

    @GetMapping("/credit-rating")
    public Integer getCreditRating(@RequestParam UUID id) {
        return retryExecutor.execute(() -> clientClient.getCreditRating(id), "gateway -> info get credit rating");
    }

    @PutMapping("/credit-rating")
    public Integer updateCreditRating(@RequestBody UpdateCreditRatingRequest request) {
        return retryExecutor.execute(() -> clientClient.updateCreditRating(request), "gateway -> info update credit rating");
    }

    @GetMapping("/token")
    public Client getByToken(@RequestParam String token) {
        return retryExecutor.execute(() -> clientClient.getByToken(token), "gateway -> info get client by token");
    }
}
