package com.OnlineBankingService.configs;

import com.OnlineBankingService.dtos.CreateClientDto;
import com.OnlineBankingService.dtos.UpdateCreditRatingRequest;
import com.OnlineBankingService.entities.Client;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "client-service", url = "http://localhost:8082/api/clients",configuration = FeignConfig.class)
public interface ClientClient {
    @GetMapping
    List<Client> getAll();

    @GetMapping("/{id}")
    Client getById(@PathVariable UUID id);

    @PostMapping
    Client create(@RequestHeader("Authorization") String authorization, @RequestBody CreateClientDto client);

    @PutMapping("/{id}")
    Client update(@RequestHeader("Authorization") String authorization, @PathVariable UUID id, @RequestBody Client client);

    @DeleteMapping("/{id}")
    void delete(@RequestHeader("Authorization") String authorization, @PathVariable UUID id);

    @PutMapping("/{id}/lock")
    Client lock(@PathVariable UUID id, @RequestHeader("Authorization") String token);

    @PutMapping("/{id}/unlock")
    Client unlock(@PathVariable UUID id, @RequestHeader("Authorization") String token);

    @GetMapping("/credit-rating")
    Integer getCreditRating(@RequestParam UUID id);

    @PutMapping("/credit-rating")
    Integer updateCreditRating(@RequestBody UpdateCreditRatingRequest request);

    @GetMapping("/token")
    Client getByToken(@RequestParam String token);
}
