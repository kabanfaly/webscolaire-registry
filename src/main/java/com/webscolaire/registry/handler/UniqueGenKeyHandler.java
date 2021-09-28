package com.webscolaire.registry.handler;

import static com.webscolaire.registry.config.WsRegisterConstants.SOURCE_ID;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.service.UniqueGenKeyService;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import reactor.core.publisher.Mono;

/**
 *
 * @since 29 avr. 2019 - 16 h 31 min 49 s
 * @author KABA N'faly
 */
@Component
public class UniqueGenKeyHandler {

    private final Logger log = LoggerFactory.getLogger(UniqueGenKeyHandler.class);

    private final UniqueGenKeyService genKeyService;

    public UniqueGenKeyHandler(UniqueGenKeyService genKeyService) {
        this.genKeyService = genKeyService;
    }

    public Mono<ServerResponse> generateUniqueKey(ServerRequest request) {
        log.info("POST call generateUniqueKey");
        return ok()
            .contentType(APPLICATION_JSON)
            .body(genKeyService.generateUniqueKey(), UniqueGenKey.class);
    }

    public Mono<ServerResponse> findByUniqueKey(ServerRequest request) {
        Mono<UniqueGenKey> uniqueGenKeyMono = genKeyService
            .findByUniqueKey(request.pathVariable(SOURCE_ID));

        return uniqueGenKeyMono.hasElement().flatMap(it -> {
            if (it) {
                return ok()
                    .contentType(APPLICATION_JSON)
                    .body(uniqueGenKeyMono, UniqueGenKey.class);
            }
            log.info("No data found for uniqueKey '{}'", request.pathVariable(SOURCE_ID));
            return notFound().build();
        });

    }
}
