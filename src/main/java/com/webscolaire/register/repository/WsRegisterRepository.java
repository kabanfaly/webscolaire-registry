/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.webscolaire.register.repository;

import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
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
}
