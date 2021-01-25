package br.com.asap.api.models;


import br.com.asap.api.config.jackson.JsonViews;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Objects;

public abstract class EntityRecord {

    @Id
    @JsonView(JsonViews.Id.class)
    @Getter @Setter
    private String id;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityRecord that = (EntityRecord) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
