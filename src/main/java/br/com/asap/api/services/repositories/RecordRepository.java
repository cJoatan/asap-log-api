package br.com.asap.api.services.repositories;

import br.com.asap.api.models.EntityRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RecordRepository<T extends EntityRecord> extends MongoRepository<T, String> {
}
