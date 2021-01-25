package br.com.asap.api.controllers;

import br.com.asap.api.config.jackson.JsonViews;
import br.com.asap.api.models.Client;
import br.com.asap.api.services.entities_services.ClientsService;
import br.com.asap.api.services.validators.ClientsValidator;
import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/clients")
public class ApiClientsController {

    private final Validator validator;
    private final ClientsValidator clientsValidator;
    private final ClientsService clientsService;

    public ApiClientsController(ClientsService clientsService, Validator validator,
                                ClientsValidator clientsValidator) {
        this.clientsService = clientsService;
        this.validator = validator;
        this.clientsValidator = clientsValidator;
    }

    @GetMapping
    @JsonView(JsonViews.Show.class)
    public Page<Client> list(@PageableDefault Pageable pageable) {
        return clientsService.findAll(pageable);
    }


    @GetMapping("{id}")
    @JsonView(JsonViews.Show.class)
    public ResponseEntity<?> show(@PathVariable String id) {
        return clientsService.findById(id)
            .map(client -> new ResponseEntity<>(client, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @JsonView(JsonViews.Show.class)
    public ResponseEntity<?> create(@RequestBody @JsonView(JsonViews.Create.class)
                                        Client client, BindingResult bindingResult) {

        validator.validate(client, bindingResult);
        clientsValidator.validate(client, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        final Client save = clientsService.create(client);

        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }

    @PutMapping("{id}")
    @JsonView(JsonViews.Show.class)
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody @JsonView(JsonViews.Create.class)
                                        Client client, BindingResult bindingResult) {

        final Optional<Client> byId = clientsService.findById(id);
        if (byId.isPresent()) {

            client.setId(id);

            validator.validate(client, bindingResult);
            clientsValidator.validate(client, bindingResult);

            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            final Client save = clientsService.update(client);

            return new ResponseEntity<>(save, HttpStatus.OK);

        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return clientsService.findById(id)
            .map(client -> {
                clientsService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
