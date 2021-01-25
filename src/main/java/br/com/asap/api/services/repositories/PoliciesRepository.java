package br.com.asap.api.services.repositories;

import br.com.asap.api.models.Policy;

import java.util.Optional;

public interface PoliciesRepository extends RecordRepository<Policy>{
    Optional<Policy> findByNumber(Integer number);
}
