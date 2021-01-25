package br.com.asap.api.services.validators;

import br.com.asap.api.models.Client;
import br.com.asap.api.services.entities_services.ClientsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Optional;

@Service
public class ClientsValidator implements Validator {

    private final ClientsService clientsService;

    @Autowired
    public ClientsValidator(ClientsService clientsService) {
        this.clientsService = clientsService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Client.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        final Client client = (Client) target;

        if (client.getId() == null) {
            createValidation(errors, client);
        } else {
            updateValidation(errors, client);
        }
    }

    private void createValidation(Errors errors, Client client) {
        final Optional<Client> byCpf = clientsService.findByCpf(client.getCpf());
        if (byCpf.isPresent()) {
            errors.rejectValue("cpf", "cpf_already_exists", "Cpf já existe");
        }
    }

    private void updateValidation(Errors errors, Client client) {
        final List<Client> all = clientsService.findAll();
        final Optional<Client> byCpf = clientsService.findByCpf(client.getCpf());
        if (byCpf.isPresent()) {
            final Client clientByCpf = byCpf.get();
            if (!clientByCpf.getId().equals(client.getId())) {
                errors.rejectValue("cpf", "cpf_already_exists", "Cpf já é usado por outro usuário");
            }
        }
    }
}
