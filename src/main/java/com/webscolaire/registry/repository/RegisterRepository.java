package com.webscolaire.registry.repository;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.Register;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public interface RegisterRepository extends ReactiveMongoRepository<Register, String> {

  Flux<Register> findByDateOfBirth(LocalDate dateOfBirth);

  Mono<Register> findOneByGeneratedNumber(String generatedNumber);

  Flux<Register> findBySourceId(String sourceId);

  Flux<Register> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);

  @Query("{ id: { $exists: true }}")
  Flux<Register> findAll(Pageable pageable);
}
