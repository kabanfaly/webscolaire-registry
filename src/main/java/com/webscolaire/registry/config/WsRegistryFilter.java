package com.webscolaire.registry.config;

import com.webscolaire.registry.exception.WsRegisterError;
import com.webscolaire.registry.exception.WsRegisterException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import static org.springframework.util.StringUtils.isEmpty;

@Configuration
public class WsRegistryFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain webFilterChain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String token = headers.getFirst("WS-TOKEN");
        if (isEmpty(token)) {
            // To validate
            //throw new WsRegisterException(WsRegisterError.WS_008);
        }
        return webFilterChain.filter(exchange);
    }
}
