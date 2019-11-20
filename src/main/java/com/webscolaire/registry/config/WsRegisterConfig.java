package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.WsRegisterConstants.PATH_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_DOB;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_TYPE;
import com.webscolaire.registry.handler.UniqueGenKeyHandler;
import com.webscolaire.registry.handler.WsRegisterHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
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
  ReactiveUserDetailsService authentication(WsRegisterProperties generatorProperties, PasswordEncoder encoder) {
    return new MapReactiveUserDetailsService(User.withUsername(generatorProperties.getUsername())
        .password(encoder.encode(generatorProperties.getPassword()))
        .roles("ADMIN")
        .build());
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  SecurityWebFilterChain webFilterChain(ServerHttpSecurity security) {
    return security
        .csrf().disable()
        .httpBasic()
        .and()
        .authorizeExchange()
        .pathMatchers("/**").authenticated()
        .pathMatchers("/actuator/**").authenticated()
        .anyExchange().permitAll()
        .and()
        .build();
  }

  @Bean
  RouterFunction<?> routerFunction(WsRegisterHandler wsRegisterHandler, UniqueGenKeyHandler uniqueGenKeyHandler) {

    // Home
    RouterFunction<ServerResponse> homeRoute = route()
        .GET("/", wsRegisterHandler::home)
        .build();

    // Routes
    RouterFunction<ServerResponse> wsRoutes = route()
        .nest(accept(APPLICATION_JSON_UTF8), builder -> {
          builder
              .POST("/register", wsRegisterHandler::registerOne)
              .POST("/registers", wsRegisterHandler::registerMany)
              .PUT("/update-source/number/{" + PATH_NUMBER + "}/sourceId/{" + PATH_SOURCE_ID + "}", wsRegisterHandler::updateSourceIdByNumber)
              .POST("/genKey", accept(APPLICATION_JSON_UTF8), uniqueGenKeyHandler::generateUniqueKey)
              .GET("/genKey/{" + PATH_SOURCE_ID + "}", uniqueGenKeyHandler::findByUniqueKey)
              .GET("/number/{" + PATH_NUMBER + "}", wsRegisterHandler::findRegisterByNumber)
              .GET("/dob/{" + PATH_DOB + "}", wsRegisterHandler::findRegisterByDateOfBirth)
              .GET("/sourceId/{" + PATH_SOURCE_ID + "}", wsRegisterHandler::findRegisterBySourceId)
              .GET("/sourceId/{" + PATH_SOURCE_ID + "}/type/{" + PATH_TYPE + "}", wsRegisterHandler::findBySourceIdAndWebscolaireType);
        }).build();

    return route().path("/", () -> wsRoutes).add(homeRoute).build();
  }
}
