package com.webscolaire.registry.repository;

import com.webscolaire.registry.domain.UniqueGenKey;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 29 avr. 2019 - 15 h 12 min 27 s
 * @author KABA N'faly
 */
public interface UniqueGenKeyRepository extends ReactiveMongoRepository<UniqueGenKey, String> {

  @Query("{ id: { $exists: true }}")
  Flux<UniqueGenKey> findAll(Pageable pageable);
}
