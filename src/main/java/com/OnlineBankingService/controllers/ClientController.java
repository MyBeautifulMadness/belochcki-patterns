package com.OnlineBankingService.controllers;

import com.OnlineBankingService.dtos.CreateClientDto;
import com.OnlineBankingService.dtos.UpdateCreditRatingRequest;
import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.services.ClientService;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PostMapping
    public Client create(@RequestBody CreateClientDto client) {
        return clientService.create(client);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PutMapping("/{id}")
    public Client update(@PathVariable UUID id, @RequestBody Client client) {
        return clientService.update(id, client);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        clientService.delete(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PatchMapping("/{id}/lock")
    public Client lock(@PathVariable UUID id) {
        return clientService.lock(id);
    }

    @PreAuthorize("hasAuthority('SCOPE_EMPLOYEE')")
    @PatchMapping("/{id}/unlock")
    public Client unlock(@PathVariable UUID id) {
        return clientService.unlock(id);
    }

    @GetMapping("/login")
    public Client getByLogin(@RequestParam String login) {
        return clientService.findByLogin(login);
    }

    @GetMapping("/token")
    public Client getByToken(@RequestParam String token) {
        return clientService.findByToken(token);
    }

    @GetMapping("/credit-rating")
    public Integer getCreditRating(@RequestParam UUID id) {
        return clientService.getCreditRating(id);
    }

    @PutMapping("/credit-rating")
    public Integer updateCreditRating(@RequestBody UpdateCreditRatingRequest request) {
        return clientService.updateCreditRating(
                request.getUserId(),
                request.getCreditRating()
        );
    }
}
