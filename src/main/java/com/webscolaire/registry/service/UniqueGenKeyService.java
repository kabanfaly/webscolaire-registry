package com.webscolaire.registry.service;

import com.webscolaire.registry.domain.UniqueGenKey;
import reactor.core.publisher.Mono;

/**
 *
 * @since 29 avr. 2019 - 15 h 25 min 45 s
 * @author KABA N'faly
 */
public interface UniqueGenKeyService {
  Mono<UniqueGenKey> generateUniqueKey();

  Mono<UniqueGenKey> findByUniqueKey(String key);


}
