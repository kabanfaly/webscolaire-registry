package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.WsRegisterConstants.PATH_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_SOURCE_ID;
import com.webscolaire.registry.handler.WsRegisterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import org.springframework.web.reactive.function.server.RouterFunction;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import org.springframework.web.reactive.function.server.ServerResponse;

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
        .pathMatchers("/ws").authenticated()
        .anyExchange().permitAll()
        .and()
        .build();
  }

  @Bean
  RouterFunction<?> routerFunction(WsRegisterHandler wsRegisterHandler) {

    RouterFunction<ServerResponse> registryPostRoutes = route()
        .nest(accept(APPLICATION_JSON_UTF8), builder -> {
          builder
              .POST("/register", wsRegisterHandler::registerOne)
              .POST("/registers", wsRegisterHandler::registerMany);
        }).build();

    RouterFunction<ServerResponse> registryGetRoutes = route()
        .GET("/", wsRegisterHandler::home)
        .GET("/number/{" + PATH_NUMBER + "}", wsRegisterHandler::findRegisterByNumber)
        .GET("/sourceId/{" + PATH_SOURCE_ID + "}", wsRegisterHandler::findRegisterBySourceID)
        .build();

    return route().path("/ws", () -> registryPostRoutes.and(registryGetRoutes)).build();
  }
}
