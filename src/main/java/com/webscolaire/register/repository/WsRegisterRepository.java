package com.webscolaire.register.repository;

import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
import java.util.List;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
public interface WsRegisterRepository extends ReactiveMongoRepository<WsRegister, String> {

    Flux<WsRegister> findByDateOfBirth(String dateOfBirth);

    Mono<WsRegister> findByGeneratedNumber(String generatedNumber);

    Flux<WsRegister> findByWebscolaireType(WebscolaireType webscolaireType);
    
    List<WsRegister> findByWebscolaireTypeAndDateOfBirth(WebscolaireType webscolaireType, String dateOfBirth);
}
