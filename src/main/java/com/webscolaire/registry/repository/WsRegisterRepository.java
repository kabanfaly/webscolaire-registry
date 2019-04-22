package com.webscolaire.registry.repository;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public interface WsRegisterRepository extends ReactiveMongoRepository<WsRegister, String> {

    Flux<WsRegister> findByDateNaissance(String dateNaissance);

    Mono<WsRegister> findByNumeroGenere(String numeroGenere);

    Flux<WsRegister> findByWebscolaireType(WebscolaireType webscolaireType);
}
