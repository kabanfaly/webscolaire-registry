package com.webscolaire.registry.service;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import java.util.List;
import org.springframework.data.domain.Pageable;
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

  Mono<WsRegister> saveRegister(WsRegisterDto registerParam);
  Mono<WsRegister> updateRegister(WsRegisterDto registerParam);
  Mono<WsRegister> updateRegisterSourceIdByNumber(String number, String newSourceId);
  Mono<WsRegister> findRegisterByNumber(String number);
  Flux<WsRegister> findByDateOfBirth(String dateOfBirth);
  Flux<WsRegister> findRegisterBySourceId(String sourceId);
  Flux<WsRegister> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);
  Flux<WsRegister> saveRegisters(List<WsRegisterDto> registerParam);
  Flux<WsRegister> findAllRegisters(Pageable page);

}
