package br.com.asap.api.services.validators;

import br.com.asap.api.models.Client;
import br.com.asap.api.models.Policy;
import br.com.asap.api.services.entities_services.ClientsService;
import br.com.asap.api.services.repositories.ClientsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.Optional;

@Service
public class PoliciesValidator implements Validator {

    private final ClientsService clientsService;

    @Autowired
    public PoliciesValidator(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Policy.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Policy policy = (Policy) target;

        if (policy.getClient() != null) {
            if (policy.getClient().getId() == null) {
                errors.rejectValue("client.id", "client_id_is_required", "Id do Cliente é obrigatório");
            } else {
                final Optional<Client> clientById = clientsService.findById(policy.getClient().getId());
                if (!clientById.isPresent()) {
                    errors.rejectValue("client", "client_not_found", "Cliente não encontrado");
                }
            }
        }
    }
}
