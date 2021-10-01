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

  Mono<WsRegisterDto> saveRegister(WsRegisterDto registerParam);
  Mono<WsRegisterDto> updateRegister(WsRegisterDto registerParam);
  Mono<WsRegisterDto> updateRegisterSourceIdByNumber(String number, String newSourceId);
  Mono<WsRegisterDto> findRegisterByNumber(String number);
  Flux<WsRegisterDto> findByDateOfBirth(String dateOfBirth);
  Flux<WsRegisterDto> findRegisterBySourceId(String sourceId);
  Flux<WsRegisterDto> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);
  Flux<WsRegisterDto> saveRegisters(List<WsRegisterDto> registerParam);
  Flux<WsRegisterDto> findAllRegisters(Pageable page);

}
