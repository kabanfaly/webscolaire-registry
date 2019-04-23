package com.webscolaire.registry.config;

import com.webscolaire.registry.handler.WsRegisterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 *
 * @since 12 avr. 2019
 * @author KABA N'faly
 */
@Configuration
public class WsRegisterConfig {

  @Bean
  ReactiveUserDetailsService authentication(WsRegisterProperties generatorProperties) {
    return new MapReactiveUserDetailsService(User.withDefaultPasswordEncoder()
        .username(generatorProperties.getUsername())
        .password(generatorProperties.getPassword())
        .roles("ADMIN")
        .build());
  }

  @Bean
  SecurityWebFilterChain webFilterChain(ServerHttpSecurity security) {
    return security
        .csrf().disable()
        .httpBasic()
        .and()
        .authorizeExchange()
        .pathMatchers("/register").authenticated()
        .anyExchange().permitAll()
        .and()
        .build();
  }

  @Bean
  RouterFunction<?> routerFunction(WsRegisterHandler wsRegisterHandler) {
    return route(GET("/"), wsRegisterHandler::home)
        .andRoute(POST("/register").and(accept(APPLICATION_JSON_UTF8)), wsRegisterHandler::registerOne)
        .andRoute(POST("/registers").and(accept(APPLICATION_JSON_UTF8)), wsRegisterHandler::registerMany);
  }
}
