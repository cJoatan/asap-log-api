package br.com.asap.api.models;

import br.com.asap.api.config.jackson.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.ToString;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;

@ToString
@Document(value = "clients")
public class Client extends EntityRecord {

    @NotEmpty(message = "Nome é obrigatório")
    @JsonView({JsonViews.Show.class, JsonViews.Create.class, JsonViews.Update.class})
    private String name;

    @Indexed(unique = true)
    @CPF(message = "CPF inválido")
    @NotEmpty(message = "CPF é obrigatório")
    @JsonView({JsonViews.Show.class, JsonViews.Create.class, JsonViews.Update.class})
    private String cpf;

    @NotEmpty(message = "Cidade é obrigatória")
    @JsonView({JsonViews.Show.class, JsonViews.Create.class, JsonViews.Update.class})
    private String city;

    @NotEmpty(message = "Estado é obrigatório")
    @JsonView({JsonViews.Show.class, JsonViews.Create.class, JsonViews.Update.class})
    private String uf;

    public String getName() {
        return name;
    }

    public Client setName(String name) {
        this.name = name;
        return this;
    }

    public String getCpf() {
        return cpf;
    }

    public Client setCpf(String cpf) {
        this.cpf = cpf;
        if (this.cpf != null) {
            this.cpf = this.cpf.trim().replace("-", "").replace(".", "");
        }
        return this;
    }

    public String getCity() {
        return city;
    }

    public Client setCity(String city) {
        this.city = city;
        return this;
    }

    public String getUf() {
        return uf;
    }

    public Client setUf(String uf) {
        this.uf = uf;
        return this;
    }

}
