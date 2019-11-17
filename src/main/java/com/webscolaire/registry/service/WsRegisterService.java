package com.webscolaire.registry.service;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import java.util.List;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * Interface définissant les services du registre à exposer.
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
public interface WsRegisterService {

  Mono<WsRegister> registerOne(WsRegisterParam registerParam);
  
  Mono<WsRegister> updateSourceIdByNumber(String number, String sourceId);
  
  Mono<WsRegister> findRegisterByNumber(String number);
  
  Flux<WsRegister> findByDateOfBirth(String dateOfBirth);
  
  Flux<WsRegister> findRegisterBySourceId(String sourceId);

  Flux<WsRegister> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);
  
  Flux<WsRegister> registerMany(List<WsRegisterParam> registerParam);
}
