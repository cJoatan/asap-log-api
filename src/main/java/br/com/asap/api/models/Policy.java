package br.com.asap.api.models;

import br.com.asap.api.config.jackson.JsonViews;
import br.com.asap.api.config.jackson.LocalDateTimeSerializer;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Document("policies")
public class Policy extends EntityRecord {

    @Indexed(unique = true)
    @NotNull(message = "Número da apólice é obrigatório")
    @JsonView(JsonViews.Show.class)
    private Integer number;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonView({JsonViews.Show.class, JsonViews.Create.class})
    @NotNull(message = "Data inicial de vigência é obrigatória")
    private LocalDateTime effectiveDateStartAt;

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonView({JsonViews.Show.class, JsonViews.Create.class})
    @NotNull(message = "Data final de vigência é obrigatória")
    private LocalDateTime effectiveDateEndAt;

    @JsonView({JsonViews.Show.class, JsonViews.Create.class})
    @NotEmpty(message = "Placa do veículo é oobrigatória")
    private String vehiclePlate;

    @DBRef
    @JsonView({JsonViews.Create.class})
    @NotNull(message = "Cliente é obrigatório")
    private Client client;

    @JsonView({JsonViews.Show.class})
    public boolean getExpired() {
        final LocalDateTime now = LocalDateTime.now();
        return now.isAfter(effectiveDateEndAt);
    }

    @JsonView({JsonViews.Show.class})
    public boolean getNotExpired() {
        return !getExpired();
    }

    @JsonView({JsonViews.Show.class})
    public Long getDaysToExpire() {
        final LocalDateTime now = LocalDateTime.now();
        return Duration.between(now, effectiveDateEndAt).toDays();
    }

    @JsonView({JsonViews.Show.class})
    public Long getDaysExpired() {
        final LocalDateTime now = LocalDateTime.now();
        return Duration.between(effectiveDateEndAt, now).toDays();
    }

    public Integer getNumber() {
        return number;
    }

    public Policy setNumber(Integer number) {
        this.number = number;
        return this;
    }

    public LocalDateTime getEffectiveDateStartAt() {
        return effectiveDateStartAt;
    }

    public Policy setEffectiveDateStartAt(LocalDateTime effectiveDateStartAt) {
        this.effectiveDateStartAt = effectiveDateStartAt;
        return this;
    }

    public LocalDateTime getEffectiveDateEndAt() {
        return effectiveDateEndAt;
    }

    public Policy setEffectiveDateEndAt(LocalDateTime effectiveDateEndAt) {
        this.effectiveDateEndAt = effectiveDateEndAt;
        return this;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public Policy setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
        return this;
    }

    public Client getClient() {
        return client;
    }

    public Policy setClient(Client client) {
        this.client = client;
        return this;
    }
}
