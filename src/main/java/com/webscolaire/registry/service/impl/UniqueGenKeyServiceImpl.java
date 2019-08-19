package com.webscolaire.registry.service.impl;

import static com.webscolaire.registry.config.WsRegisterConstants.AUTHORIZED_CHARACTERS;
import static com.webscolaire.registry.config.WsRegisterConstants.TOKEN_LENGTH;
import static com.webscolaire.registry.config.WsRegisterConstants.UNIQUE_KEY;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.utils.WsRegistryUtils;
import javax.inject.Inject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.mongodb.core.MongoTemplate;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import com.webscolaire.registry.service.UniqueGenKeyService;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import java.util.UUID;

/**
 *
 * @since 29 avr. 2019 - 15 h 29 min 45 s
 * @author KABA N'faly
 */
@Service
@Data
@NoArgsConstructor
@Transactional
@Log4j2
public class UniqueGenKeyServiceImpl implements UniqueGenKeyService {

  @Inject
  private UniqueGenKeyRepository uniqueKeyRepository;

  @Inject
  private MongoTemplate mongoTemplate;

  @Override
  public Mono<UniqueGenKey> generateUniqueKey() {
    log.info("Request to build uniqueKey");
    
    // Génération d'un token
    String token = UUID.randomUUID().toString();
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
