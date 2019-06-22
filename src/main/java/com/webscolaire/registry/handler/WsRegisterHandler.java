package com.webscolaire.registry.handler;

import static com.webscolaire.registry.config.WsRegisterConstants.PATH_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_DOB;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.exception.WsRegisterException;
import com.webscolaire.registry.service.WsRegisterService;
import javax.inject.Inject;
import lombok.extern.log4j.Log4j2;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.ServerResponse.badRequest;
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

  @Inject
  private WsRegisterService registerService;

  public Mono<ServerResponse> home(ServerRequest request) {
    return ok().body(Mono.just("Registre WebScolaire"), String.class);
  }

  public Mono<ServerResponse> registerOne(ServerRequest request) {
    log.info("POST call registerOne");
        return ok()
        .contentType(APPLICATION_JSON_UTF8)
        .body(fromPublisher(request.bodyToMono(WsRegisterParam.class)
            .flatMap(registerService::registerOne), WsRegister.class));

  }

  public Mono<ServerResponse> registerMany(ServerRequest request) {
    log.info("POST call registerMany");
    return ok()
        .contentType(APPLICATION_JSON_UTF8)
        .body(fromPublisher(request.bodyToFlux(WsRegisterParam.class)
            .buffer().flatMap(registerService::registerMany), WsRegister.class));
  }

  public Mono<ServerResponse> findRegisterByNumber(ServerRequest request) {

    Mono<WsRegister> wsRegisterMono = registerService.findRegisterByNumber(request.pathVariable(PATH_NUMBER));

    return wsRegisterMono.hasElement().flatMap(it -> {
      if (it) {
        return ok()
            .contentType(APPLICATION_JSON_UTF8)
            .body(wsRegisterMono, WsRegister.class);
      }
      log.info("No data found for number '{}'", request.pathVariable(PATH_NUMBER));
      return notFound().build();
    });

  }

  public Mono<ServerResponse> findRegisterBySourceId(ServerRequest request) {

    Flux<WsRegister> wsRegisterFlux = registerService.findRegisterBySourceId(request.pathVariable(PATH_SOURCE_ID));

    return wsRegisterFlux.hasElements().flatMap(it -> {
      if (it) {
        return ok()
            .contentType(APPLICATION_JSON_UTF8)
            .body(wsRegisterFlux, WsRegister.class);
      }
      log.info("No data found for sourceId '{}'", request.pathVariable(PATH_SOURCE_ID));
      return notFound().build();
    });

  }

  public Mono<ServerResponse> findRegisterByDateOfBirth(ServerRequest request) {
    Flux<WsRegister> wsRegisterFlux = registerService.findByDateOfBirth(request.pathVariable(PATH_DOB));

    return wsRegisterFlux.hasElements().flatMap(it -> {
      if (it) {
        return ok()
            .contentType(APPLICATION_JSON_UTF8)
            .body(wsRegisterFlux, WsRegister.class);
      }
      log.info("No data found for sourceId '{}'", request.pathVariable(PATH_DOB));
      return notFound().build();
    });
 
  }

}
