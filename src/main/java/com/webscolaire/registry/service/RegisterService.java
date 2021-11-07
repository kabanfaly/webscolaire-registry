package com.webscolaire.registry.service;

import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.dto.RegisterDto;
import com.webscolaire.registry.domain.dto.SourceIdUpdateDto;

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
public interface RegisterService {

  Mono<RegisterDto> saveRegister(RegisterDto registerParam);

  Mono<RegisterDto> updateRegister(RegisterDto registerParam);

  Mono<RegisterDto> updateRegisterSourceIdByNumber(SourceIdUpdateDto sourceIdUpdateDto);

  Mono<RegisterDto> findRegisterByNumber(String number);

  Flux<RegisterDto> findByDateOfBirth(String dateOfBirth);

  Flux<RegisterDto> findRegisterBySourceId(String sourceId);

  Flux<RegisterDto> findBySourceIdAndWebscolaireType(String sourceId, WebscolaireType webscolaireType);

  Flux<RegisterDto> saveRegisters(List<RegisterDto> registerParam);

  Flux<RegisterDto> findAllRegisters(Pageable page);

  Mono<Void> deleteAll();

}
