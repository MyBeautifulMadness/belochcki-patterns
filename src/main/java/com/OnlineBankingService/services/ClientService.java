package com.OnlineBankingService.services;

import com.OnlineBankingService.dtos.CreateClientDto;
import com.OnlineBankingService.entities.Client;
import com.OnlineBankingService.entities.Status;
import com.OnlineBankingService.repositories.ClientRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientService {

    private final ClientRepository clientRepository;

    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public List<Client> findAll() {
        return clientRepository.findAll();
    }

    public Client findById(UUID id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client findByLogin(String login) {
        return clientRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client findByToken(String token) {
        return clientRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Client create(CreateClientDto dto) {
        Client client = new Client();
        client.id = UUID.randomUUID();
        client.name = dto.name;
        client.login = dto.login;
        client.password = dto.password;
        client.status = Status.UNLOCKED;

        return clientRepository.save(client);
    }

    public Client update(UUID id, Client updated) {
        Client client = findById(id);
        client.login = updated.login;
        client.name = updated.name;
        client.password = updated.password;
        client.status = updated.status;

        return clientRepository.save(client);
    }

    public void delete(UUID id) {
        clientRepository.deleteById(id);
    }

    public Client lock(UUID id) {
        Client client = findById(id);
        client.status = Status.LOCKED;
        return clientRepository.save(client);
    }

    public Client unlock(UUID id) {
        Client client = findById(id);
        client.status = Status.UNLOCKED;
        return clientRepository.save(client);
    }

    public Integer getCreditRating(UUID userId) {
        return clientRepository.findById(userId)
                .map(Client::getCreditRating)
                .orElseThrow(() -> new RuntimeException("Client not found"));
    }

    public Integer updateCreditRating(UUID userId, Integer newValue) {
        Client user = clientRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        user.setCreditRating(newValue);
        clientRepository.save(user);

        return newValue;
    }
}