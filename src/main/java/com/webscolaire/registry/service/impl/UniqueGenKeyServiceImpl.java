package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import lombok.extern.log4j.Log4j2;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 *
 * @since 29 avr. 2019 - 15 h 29 min 45 s
 * @author KABA N'faly
 */
@Log4j2
public class UniqueGenKeyServiceImpl implements UniqueGenKeyService {

    private final UniqueGenKeyRepository uniqueKeyRepository;

    public UniqueGenKeyServiceImpl(UniqueGenKeyRepository uniqueKeyRepository) {
        this.uniqueKeyRepository = uniqueKeyRepository;
    }

    @Override
    public Mono<UniqueGenKey> generateUniqueKey() {
        log.info("Request to build uniqueKey");

        // Génération d'un token
        String token = UUID.randomUUID().toString().replace("-", "");
        UniqueGenKey genKey = new UniqueGenKey().setUniqueKey(token);

        uniqueKeyRepository.save(genKey).subscribe();

        return Mono.just(genKey);
    }

    @Override
    public Mono<UniqueGenKey> findByUniqueKey(String key) {
        log.info("Request to find UniqueGenKey by key : {}", key);
        return uniqueKeyRepository.findOneByUniqueKey(key);
    }
}
