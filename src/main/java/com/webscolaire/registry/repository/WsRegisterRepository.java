package com.webscolaire.registry.repository;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
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
public interface WsRegisterRepository extends ReactiveMongoRepository<WsRegister, String> {

  Flux<WsRegister> findByDateOfBirth(LocalDate dateOfBirth);

  Mono<WsRegister> findOneByGeneratedNumber(String generatedNumber);

  Flux<WsRegister> findBySourceId(String sourceId);

  Flux<WsRegister> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);

  @Query("{ id: { $exists: true }}")
  Flux<WsRegister> findAll(Pageable pageable);
}
