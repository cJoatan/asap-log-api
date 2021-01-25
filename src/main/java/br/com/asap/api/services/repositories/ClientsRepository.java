package br.com.asap.api.services.repositories;

import br.com.asap.api.models.Client;

import java.util.Optional;

public interface ClientsRepository extends RecordRepository<Client> {
    Optional<Client> findByCpf(String cpf);
}
