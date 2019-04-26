package com.webscolaire.registry.handler;

import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.service.WsRegisterService;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import reactor.core.publisher.Mono;

/**
 *
 * @since 24 avr. 2019 - 10 h 16 min 14 s
 * @author KABA N'faly
 */
@Component
@Slf4j
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

}
