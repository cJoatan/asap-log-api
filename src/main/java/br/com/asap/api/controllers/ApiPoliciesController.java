package br.com.asap.api.controllers;

import br.com.asap.api.config.jackson.JsonViews;
import br.com.asap.api.models.Policy;
import br.com.asap.api.services.entities_services.PoliciesService;
import br.com.asap.api.services.validators.PoliciesValidator;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("api/policies")
public class ApiPoliciesController {

    private final PoliciesValidator policiesValidator;
    private final Validator validator;
    private final PoliciesService policiesService;

    public ApiPoliciesController(PoliciesService policiesService,
                                 PoliciesValidator policiesValidator,
                                 Validator validator) {
        this.policiesService = policiesService;
        this.policiesValidator = policiesValidator;
        this.validator = validator;
    }

    @JsonView(JsonViews.Show.class)
    @GetMapping
    public Page<Policy> list(Pageable pageable) {
        return policiesService.findAll(pageable);
    }

    @JsonView(JsonViews.Show.class)
    @GetMapping("{id}")
    public ResponseEntity<?> show(@PathVariable String id) {
        return policiesService.findById(id)
            .map(policy -> new ResponseEntity<>(policy, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @JsonView(JsonViews.Show.class)
    @GetMapping("number/{number}")
    public ResponseEntity<?> showByNumber(@PathVariable Integer number) {
        return policiesService.findByNumber(number)
            .map(policy -> new ResponseEntity<>(policy, HttpStatus.OK))
            .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    @JsonView(JsonViews.Show.class)
    public ResponseEntity<?> create(@RequestBody @JsonView(JsonViews.Create.class)
                                        Policy policy, BindingResult bindingResult) {

        final Integer number = policiesService.generateNumber();
        policy.setNumber(number);

        validator.validate(policy, bindingResult);
        policiesValidator.validate(policy, bindingResult);
        if (bindingResult.hasErrors()) {
            return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
        }

        final Policy save = policiesService.create(policy);

        return new ResponseEntity<>(save, HttpStatus.CREATED);
    }


    @PutMapping("{id}")
    @JsonView(JsonViews.Show.class)
    public ResponseEntity<?> update(@PathVariable String id,
                                    @RequestBody @JsonView(JsonViews.Create.class)
                                        Policy policy, BindingResult bindingResult) {

        final Optional<Policy> byId = policiesService.findById(id);
        if (byId.isPresent()) {

            final Policy policyById = byId.get();
            policy.setId(id);
            policy.setNumber(policyById.getNumber());

            validator.validate(policy, bindingResult);
            policiesValidator.validate(policy, bindingResult);
            if (bindingResult.hasErrors()) {
                return new ResponseEntity<>(bindingResult.getAllErrors(), HttpStatus.UNPROCESSABLE_ENTITY);
            }

            final Policy save = policiesService.update(policy);

            return new ResponseEntity<>(save, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        return policiesService.findById(id)
            .map(client -> {
                policiesService.deleteById(id);
                return new ResponseEntity<>(HttpStatus.OK);
            }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }


}
