package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 *
 * @since 29 avr. 2019 - 15 h 29 min 45 s
 * @author KABA N'faly
 */
public class UniqueGenKeyServiceImpl implements UniqueGenKeyService {

    private final Logger log = LoggerFactory.getLogger(UniqueGenKeyServiceImpl.class);

    private final UniqueGenKeyRepository uniqueKeyRepository;

    public UniqueGenKeyServiceImpl(UniqueGenKeyRepository uniqueKeyRepository) {
        this.uniqueKeyRepository = uniqueKeyRepository;
    }

    @Override
    public Mono<UniqueGenKey> generateUniqueKey() {
        log.info("Request to build uniqueKey");

        return Mono.just(UUID.randomUUID().toString().replace("-", ""))
            .map(token -> new UniqueGenKey().setUniqueKey(token))
            .flatMap(uniqueKeyRepository::save);
    }

    @Override
    public Mono<UniqueGenKey> findByUniqueKey(String key) {
        log.info("Request to find UniqueGenKey by key : {}", key);
        return uniqueKeyRepository.findOneByUniqueKey(key);
    }
}
