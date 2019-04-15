package com.webscolaire.register.router;

import com.webscolaire.register.config.WsRegisterProperties;
import com.webscolaire.register.domain.WsRegisterParam;
import com.webscolaire.register.service.WsRegisterService;
import org.springframework.context.annotation.Bean;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import static org.springframework.web.reactive.function.BodyExtractors.toMono;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;
import reactor.core.publisher.Flux;

/**
 *
 * @since 14 avr. 2019
 * @author KABA N'faly
 */
@Component
public class WsRegisterRouter {

    @Bean
    RouterFunction<?> routerFunction(WsRegisterService rs) {
        return route(GET("/"),
                req -> ok().body(Flux.just("Registre WebScolaire"), String.class))
                .andRoute(POST("/register").and(accept(APPLICATION_JSON)),
                        req -> req
                                .body(toMono(WsRegisterParam.class))
                                .doOnNext(rs::register)
                                .then(ok().contentType(APPLICATION_JSON).build()));
    }
}
