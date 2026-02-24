package com.OnlineBankingService.services;

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

    public Client create(Client client) {
        client.id = UUID.randomUUID();
        client.status = Status.UNLOCKED;
        return clientRepository.save(client);
    }

    public Client update(UUID id, Client updated) {
        Client client = findById(id);
        client.login = updated.login;
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
}
