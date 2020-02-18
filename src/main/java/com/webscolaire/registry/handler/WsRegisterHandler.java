package com.webscolaire.registry.handler;

import static com.webscolaire.registry.config.WsRegisterConstants.DEFAULT_PAGE;
import static com.webscolaire.registry.config.WsRegisterConstants.DEFAULT_SIZE;
import static com.webscolaire.registry.config.WsRegisterConstants.PAGE;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_DOB;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_TYPE;
import static com.webscolaire.registry.config.WsRegisterConstants.SIZE;
import com.webscolaire.registry.domain.WebscolaireType;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import com.webscolaire.registry.service.WsRegisterService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;

import static com.webscolaire.registry.domain.WebscolaireType.*;
import static java.lang.Integer.parseInt;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 *
 * @since 24 avr. 2019 - 10 h 16 min 14 s
 * @author KABA N'faly
 */
@Component
@Log4j2
public class WsRegisterHandler {

    private final WsRegisterService registerService;

    public WsRegisterHandler(WsRegisterService registerService) {
        this.registerService = registerService;
    }

    public Mono<ServerResponse> home(ServerRequest request) {
        return ok().body(Mono.just("Registre WebScolaire"), String.class);
    }

    public Mono<ServerResponse> registerOne(ServerRequest request) {
        log.info("POST call registerOne");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToMono(WsRegisterDto.class)
                        .flatMap(registerService::registerOne), WsRegister.class));

    }

    public Mono<ServerResponse> updateRegister(ServerRequest request) {
        log.info("POST call registerOne");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToMono(WsRegisterDto.class)
                        .flatMap(registerService::updateRegister), WsRegister.class));
    }

    public Mono<ServerResponse> findAllRegisters(ServerRequest request) {
        log.info("GET call findAllRegisters");
        int page = parseInt(request.queryParam(PAGE).orElse(DEFAULT_PAGE));
        int size = parseInt(request.queryParam(SIZE).orElse(DEFAULT_SIZE));
        return ok().contentType(APPLICATION_JSON)
                .body(registerService.findAllRegisters(PageRequest.of(page, size)), WsRegister.class);
    }

    public Mono<ServerResponse> updateSourceIdByNumber(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON)
                .body(registerService.updateSourceIdByNumber(request.pathVariable(PATH_NUMBER),
                        request.pathVariable(PATH_SOURCE_ID)), WsRegister.class);
    }

    public Mono<ServerResponse> registerMany(ServerRequest request) {
        log.info("POST call registerMany");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToFlux(WsRegisterDto.class)
                        .buffer().flatMap(registerService::registerMany), WsRegister.class));
    }

    public Mono<ServerResponse> findRegisterByNumber(ServerRequest request) {
        return registerService.findRegisterByNumber(request.pathVariable(PATH_NUMBER))
                .flatMap(wsRegister -> ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(wsRegister), WsRegister.class))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> findRegisterBySourceId(ServerRequest request) {

        Flux<WsRegister> wsRegisterFlux = registerService
                .findRegisterBySourceId(request.pathVariable(PATH_SOURCE_ID));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, WsRegister.class);
            }
            log.info("No data found for sourceId '{}'", request.pathVariable(PATH_SOURCE_ID));
            return notFound().build();
        });

    }

    public Mono<ServerResponse> findRegisterByDateOfBirth(ServerRequest request) {
        Flux<WsRegister> wsRegisterFlux = registerService.findByDateOfBirth(request.pathVariable(PATH_DOB));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, WsRegister.class);
            }
            log.info("No data found for dateOfBirth '{}'", request.pathVariable(PATH_DOB));
            return notFound().build();
        });

    }

    public Mono<ServerResponse> findBySourceIdAndWebscolaireType(ServerRequest request) {
        Flux<WsRegister> wsRegisterFlux = registerService
                .findBySourceIdAndWebscolaireType(request.pathVariable(PATH_SOURCE_ID), getTypeFromString(request.pathVariable(PATH_TYPE)));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, WsRegister.class);
            }
            log.info("No data found for sourceId '{}' and type '{}'", request.pathVariable(PATH_SOURCE_ID), request.pathVariable(PATH_TYPE));
            return notFound().build();
        });

    }

}
