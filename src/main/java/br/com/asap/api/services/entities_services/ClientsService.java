package br.com.asap.api.services.entities_services;

import br.com.asap.api.models.Client;
import br.com.asap.api.services.repositories.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientsService {

    private final ClientsRepository clientsRepository;

    @Autowired
    public ClientsService(ClientsRepository clientsRepository) {
        this.clientsRepository = clientsRepository;
    }

    public Client create(Client client) {
        client.setId(null);
        return clientsRepository.save(client);
    }

    public Client update(Client client) {
        return clientsRepository.save(client);
    }

    public Optional<Client> findByCpf(String cpf) {
        return clientsRepository.findByCpf(cpf);
    }

    public Optional<Client> findById(String id) {
        return clientsRepository.findById(id);
    }

    public List<Client> findAll() {
        return clientsRepository.findAll();
    }

    public Page<Client> findAll(Pageable pageable) {
        return clientsRepository.findAll(pageable);
    }

    public void deleteById(String id) {
        clientsRepository.deleteById(id);
    }
}
