package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 *
 * @since 29 avr. 2019 - 15 h 29 min 45 s
 * @author KABA N'faly
 */
@Log4j2
@AllArgsConstructor
public class UniqueGenKeyServiceImpl implements UniqueGenKeyService {

    private final UniqueGenKeyRepository uniqueKeyRepository;

    @Override
    public Mono<UniqueGenKey> generateUniqueKey() {
        log.info("Request to build uniqueKey");

        // Génération d'un token
        String token = UUID.randomUUID().toString().replace("-", "");
        return uniqueKeyRepository.save(new UniqueGenKey().setUniqueKey(token));
    }

    @Override
    public Mono<UniqueGenKey> findByUniqueKey(String key) {
        log.info("Request to find UniqueGenKey by key : {}", key);
        return uniqueKeyRepository.findOneByUniqueKey(key);
    }
}
