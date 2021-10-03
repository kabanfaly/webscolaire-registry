package com.webscolaire.registry.handler;

import static com.webscolaire.registry.config.Constants.DEFAULT_PAGE;
import static com.webscolaire.registry.config.Constants.DEFAULT_SIZE;
import static com.webscolaire.registry.config.Constants.PAGE;
import static com.webscolaire.registry.config.Constants.PATH_NUMBER;
import static com.webscolaire.registry.config.Constants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.Constants.PATH_DOB;
import static com.webscolaire.registry.config.Constants.PATH_TYPE;
import static com.webscolaire.registry.config.Constants.SIZE;

import com.webscolaire.registry.domain.Register;
import com.webscolaire.registry.domain.dto.RegisterDto;
import com.webscolaire.registry.domain.dto.SourceIdUpdateDto;
import com.webscolaire.registry.service.RegisterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class RegisterHandler {

    private final Logger log = LoggerFactory.getLogger(RegisterHandler.class);

    private final RegisterService registerService;

    public RegisterHandler(RegisterService registerService) {
        this.registerService = registerService;
    }

    public Mono<ServerResponse> home(ServerRequest request) {
        return ok().body(Mono.just("Registre WebScolaire"), String.class);
    }

    public Mono<ServerResponse> registerOne(ServerRequest request) {
        log.info("POST call registerOne");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToMono(RegisterDto.class)
                        .flatMap(registerService::saveRegister), RegisterDto.class));

    }

    public Mono<ServerResponse> updateRegister(ServerRequest request) {
        log.info("POST call registerOne");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToMono(RegisterDto.class)
                        .flatMap(registerService::updateRegister), RegisterDto.class));
    }

    public Mono<ServerResponse> findAllRegisters(ServerRequest request) {
        log.info("GET call findAllRegisters");
        int page = parseInt(request.queryParam(PAGE).orElse(DEFAULT_PAGE));
        int size = parseInt(request.queryParam(SIZE).orElse(DEFAULT_SIZE));
        return ok().contentType(APPLICATION_JSON)
                .body(registerService.findAllRegisters(PageRequest.of(page, size)), Register.class);
    }

    public Mono<ServerResponse> updateSourceIdByNumber(ServerRequest request) {
        return ok().contentType(APPLICATION_JSON)
            .body(fromPublisher(request.bodyToMono(SourceIdUpdateDto.class)
                .flatMap(registerService::updateRegisterSourceIdByNumber), RegisterDto.class));
    }

    public Mono<ServerResponse> registerMany(ServerRequest request) {
        log.info("POST call registerMany");
        return ok().contentType(APPLICATION_JSON)
                .body(fromPublisher(request.bodyToFlux(RegisterDto.class)
                        .buffer().flatMap(registerService::saveRegisters), RegisterDto.class));
    }

    public Mono<ServerResponse> findRegisterByNumber(ServerRequest request) {
        return registerService.findRegisterByNumber(request.pathVariable(PATH_NUMBER))
                .flatMap(wsRegister -> ok()
                        .contentType(APPLICATION_JSON)
                        .body(Mono.just(wsRegister), Register.class))
                .switchIfEmpty(notFound().build());
    }

    public Mono<ServerResponse> findRegisterBySourceId(ServerRequest request) {

        Flux<RegisterDto> wsRegisterFlux = registerService
                .findRegisterBySourceId(request.pathVariable(PATH_SOURCE_ID));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, Register.class);
            }
            log.info("No data found for sourceId '{}'", request.pathVariable(PATH_SOURCE_ID));
            return notFound().build();
        });

    }

    public Mono<ServerResponse> findRegisterByDateOfBirth(ServerRequest request) {
        Flux<RegisterDto> wsRegisterFlux = registerService.findByDateOfBirth(request.pathVariable(PATH_DOB));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, Register.class);
            }
            log.info("No data found for dateOfBirth '{}'", request.pathVariable(PATH_DOB));
            return notFound().build();
        });

    }

    public Mono<ServerResponse> findBySourceIdAndWebscolaireType(ServerRequest request) {
        Flux<RegisterDto> wsRegisterFlux = registerService
                .findBySourceIdAndWebscolaireType(request.pathVariable(PATH_SOURCE_ID), getTypeFromString(request.pathVariable(PATH_TYPE)));

        return wsRegisterFlux.hasElements().flatMap(it -> {
            if (it) {
                return ok().contentType(APPLICATION_JSON).body(wsRegisterFlux, Register.class);
            }
            log.info("No data found for sourceId '{}' and type '{}'", request.pathVariable(PATH_SOURCE_ID), request.pathVariable(PATH_TYPE));
            return notFound().build();
        });

    }

}
