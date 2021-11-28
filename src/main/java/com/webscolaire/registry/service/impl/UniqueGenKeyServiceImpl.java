package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.dto.UniqueGenKeyDto;
import com.webscolaire.registry.mapper.UniqueGenKeyMapper;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 29 avr. 2019 - 15 h 29 min 45 s
 * @author KABA N'faly
 */
public class UniqueGenKeyServiceImpl implements UniqueGenKeyService {

    private final Logger log = LoggerFactory.getLogger(UniqueGenKeyServiceImpl.class);

    private final UniqueGenKeyRepository uniqueKeyRepository;
    private final UniqueGenKeyMapper uniqueGenKeyMapper;

    public UniqueGenKeyServiceImpl(UniqueGenKeyRepository uniqueKeyRepository, UniqueGenKeyMapper uniqueGenKeyMapper) {
        this.uniqueKeyRepository = uniqueKeyRepository;
        this.uniqueGenKeyMapper = uniqueGenKeyMapper;
    }

    @Override
    public Mono<UniqueGenKeyDto> generateUniqueKey() {
        log.info("Request to build uniqueKey");
        return uniqueKeyRepository.save(new UniqueGenKey())
            .map(uniqueGenKeyMapper::toDto);
    }

    @Override
    public Mono<UniqueGenKeyDto> findByUniqueKey(String key) {
        log.info("Request to find UniqueGenKey by key : {}", key);
        return uniqueKeyRepository.findById(key)
            .map(uniqueGenKeyMapper::toDto);
    }

    @Override
    public Flux<UniqueGenKeyDto> findAllUniqueGenKey(Pageable page) {
        log.info("Request to find all UniqueGenKey");
        return uniqueKeyRepository.findAll(page)
            .map(uniqueGenKeyMapper::toDto);
    }
}
