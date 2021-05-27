package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.WsRegisterConstants.PATH_NUMBER;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_DOB;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.WsRegisterConstants.PATH_TYPE;

import com.webscolaire.registry.domain.dto.WsRegisterMapper;
import com.webscolaire.registry.handler.UniqueGenKeyHandler;
import com.webscolaire.registry.handler.WsRegisterHandler;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import com.webscolaire.registry.service.WsRegisterService;
import com.webscolaire.registry.service.impl.UniqueGenKeyServiceImpl;
import com.webscolaire.registry.service.impl.WsRegisterServiceImpl;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
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
@ConfigurationPropertiesScan
@EnableReactiveMongoAuditing
@EnableReactiveMongoRepositories(basePackages ="com.webscolaire.registry.repository")
public class WsRegisterConfig {

    @Bean
    public WsRegisterService wsRegisterService(WsRegisterRepository registerRepository, UniqueGenKeyRepository uniqueGenKeyRepository,
                                               ReactiveMongoTemplate reactiveMongoTemplate, WsRegisterMapper wsRegisterMapper) {
        return new WsRegisterServiceImpl(registerRepository, uniqueGenKeyRepository, reactiveMongoTemplate, wsRegisterMapper);
    }

    @Bean
    public UniqueGenKeyService uniqueGenKeyService(UniqueGenKeyRepository uniqueKeyRepository) {
        return new UniqueGenKeyServiceImpl(uniqueKeyRepository);
    }

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
            .nest(accept(APPLICATION_JSON), builder -> {
                builder
                    .POST("/register", wsRegisterHandler::registerOne)
                    .PUT("/register", wsRegisterHandler::updateRegister)
                    .GET("/registers", wsRegisterHandler::findAllRegisters)
                    .POST("/registers", wsRegisterHandler::registerMany)
                    .PUT("/update-source/number/{" + PATH_NUMBER + "}/sourceId/{" + PATH_SOURCE_ID + "}", wsRegisterHandler::updateSourceIdByNumber)
                    .POST("/genKey", accept(APPLICATION_JSON), uniqueGenKeyHandler::generateUniqueKey)
                    .GET("/genKey/{" + PATH_SOURCE_ID + "}", uniqueGenKeyHandler::findByUniqueKey)
                    .GET("/number/{" + PATH_NUMBER + "}", wsRegisterHandler::findRegisterByNumber)
                    .GET("/dob/{" + PATH_DOB + "}", wsRegisterHandler::findRegisterByDateOfBirth)
                    .GET("/sourceId/{" + PATH_SOURCE_ID + "}", wsRegisterHandler::findRegisterBySourceId)
                    .GET("/sourceId/{" + PATH_SOURCE_ID + "}/type/{" + PATH_TYPE + "}", wsRegisterHandler::findBySourceIdAndWebscolaireType);
            }).build();

        return route().path("/", () -> wsRoutes).add(homeRoute).build();
    }
}
