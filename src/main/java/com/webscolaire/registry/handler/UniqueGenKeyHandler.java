package com.webscolaire.registry.handler;

import com.webscolaire.registry.domain.dto.UniqueGenKeyDto;
import com.webscolaire.registry.service.UniqueGenKeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static com.webscolaire.registry.config.Constants.*;
import static java.lang.Integer.parseInt;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

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
            .body(genKeyService.generateUniqueKey(), UniqueGenKeyDto.class);
    }

    public Mono<ServerResponse> findByUniqueKey(ServerRequest request) {
        Mono<UniqueGenKeyDto> uniqueGenKeyMono = genKeyService
            .findByUniqueKey(request.pathVariable(SOURCE_ID));

        return uniqueGenKeyMono.hasElement().flatMap(it -> {
            if (it) {
                return ok()
                    .contentType(APPLICATION_JSON)
                    .body(uniqueGenKeyMono, UniqueGenKeyDto.class);
            }
            log.info("No data found for uniqueKey '{}'", request.pathVariable(SOURCE_ID));
            return notFound().build();
        });
    }

    public Mono<ServerResponse> findAllUniqueKeys(ServerRequest request) {
        log.info("GET call findAllUniqueGenKey");
        int page = parseInt(request.queryParam(PAGE).orElse(DEFAULT_PAGE));
        int size = parseInt(request.queryParam(SIZE).orElse(DEFAULT_SIZE));
        return ok().contentType(APPLICATION_JSON)
            .body(genKeyService.findAllUniqueGenKey(PageRequest.of(page, size)), UniqueGenKeyDto.class);
    }
}
