package com.webscolaire.registry.service;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.dto.UniqueGenKeyDto;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 29 avr. 2019 - 15 h 25 min 45 s
 * @author KABA N'faly
 */
public interface UniqueGenKeyService {

  Mono<UniqueGenKeyDto> generateUniqueKey();

  Mono<UniqueGenKeyDto> findByUniqueKey(String key);

  Flux<UniqueGenKeyDto> findAllUniqueGenKey(Pageable page);
}
