package com.webscolaire.registry.router;

import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterParam;
import com.webscolaire.registry.service.WsRegisterService;
import java.util.List;
import org.springframework.context.annotation.Bean;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import static org.springframework.web.reactive.function.BodyInserters.fromPublisher;
import reactor.core.publisher.Mono;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@Component
public class WsRegisterRouter {

    @Bean
    RouterFunction<?> routerFunction(WsRegisterService rs) {
        return route(GET("/"), req -> ok().body(Mono.just("Registre WebScolaire"), String.class))
                .andRoute(POST("/register").and(accept(APPLICATION_JSON)),
                        req -> ok()
                                .contentType(APPLICATION_JSON)
                                .body(fromPublisher(req.bodyToMono(WsRegisterParam.class).flatMap(rs::registerOne), WsRegister.class)))
                .andRoute(POST("/registers").and(accept(APPLICATION_JSON)), 
                         req -> ok()
                                .contentType(APPLICATION_JSON)
                                .body(fromPublisher(req.bodyToFlux(WsRegisterParam.class).buffer().flatMap(rs::registerMany), WsRegister.class)))
                                   
                        ;
    }

}
