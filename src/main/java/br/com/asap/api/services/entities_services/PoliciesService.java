package br.com.asap.api.services.entities_services;

import br.com.asap.api.models.Policy;
import br.com.asap.api.services.repositories.PoliciesRepository;
import com.github.javafaker.Faker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PoliciesService {

    private final PoliciesRepository policiesRepository;
    private final Faker faker;

    @Autowired
    public PoliciesService(PoliciesRepository policiesRepository, Faker faker) {
        this.policiesRepository = policiesRepository;
        this.faker = faker;
    }

    public List<Policy> findAll() {
        return policiesRepository.findAll();
    }

    public Page<Policy> findAll(Pageable pageable) {
        return policiesRepository.findAll(pageable);
    }

    public Optional<Policy> findById(String id) {
        return policiesRepository.findById(id);
    }

    public Policy create(Policy policy) {
        policy.setId(null);
        return policiesRepository.save(policy);
    }

    public Policy update(Policy policy) {
        return policiesRepository.save(policy);
    }

    public Optional<Policy> findByNumber(Integer number) {
        return policiesRepository.findByNumber(number);
    }

    public Integer generateNumber() {
        final int number = faker.number().numberBetween(0, 100000);
        Optional<Policy> byNumber;
        do {
            byNumber = policiesRepository.findByNumber(number);
        } while(byNumber.isPresent());
        return number;
    }

    public void deleteById(String id) {
        policiesRepository.deleteById(id);
    }
}

