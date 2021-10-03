package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.Constants.PATH_NUMBER;
import static com.webscolaire.registry.config.Constants.PATH_DOB;
import static com.webscolaire.registry.config.Constants.PATH_SOURCE_ID;
import static com.webscolaire.registry.config.Constants.PATH_TYPE;

import com.webscolaire.registry.mapper.RegisterMapper;
import com.webscolaire.registry.handler.UniqueGenKeyHandler;
import com.webscolaire.registry.handler.RegisterHandler;
import com.webscolaire.registry.mapper.UniqueGenKeyMapper;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.RegisterRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import com.webscolaire.registry.service.RegisterService;
import com.webscolaire.registry.service.impl.UniqueGenKeyServiceImpl;
import com.webscolaire.registry.service.impl.RegisterServiceImpl;
import com.webscolaire.registry.validator.GenericValidator;
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
public class RegisterConfig {

    @Bean
    public RegisterService wsRegisterService(RegisterRepository registerRepository, UniqueGenKeyRepository uniqueGenKeyRepository,
                                             ReactiveMongoTemplate reactiveMongoTemplate, RegisterMapper registerMapper, GenericValidator validator) {
        return new RegisterServiceImpl(registerRepository, uniqueGenKeyRepository, reactiveMongoTemplate, registerMapper, validator);
    }

    @Bean
    public UniqueGenKeyService uniqueGenKeyService(UniqueGenKeyRepository uniqueKeyRepository, UniqueGenKeyMapper uniqueGenKeyMapper) {
        return new UniqueGenKeyServiceImpl(uniqueKeyRepository, uniqueGenKeyMapper);
    }

    @Bean
    ReactiveUserDetailsService authentication(RegisterProperties generatorProperties, PasswordEncoder encoder) {
        return new MapReactiveUserDetailsService(User.withUsername(generatorProperties.username)
            .password(encoder.encode(generatorProperties.password))
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
            .pathMatchers("/actuator/**").permitAll()
            .and()
            .build();
    }

    @Bean
    RouterFunction<?> routerFunction(RegisterHandler registerHandler, UniqueGenKeyHandler uniqueGenKeyHandler) {

        // Home
        RouterFunction<ServerResponse> homeRoute = route()
            .GET("/", registerHandler::home)
            .build();

        // Routes
        RouterFunction<ServerResponse> wsRoutes = route()
            .nest(accept(APPLICATION_JSON), builder -> {
                builder
                    .POST("/register", registerHandler::registerOne)
                    .PUT("/register", registerHandler::updateRegister)
                    .GET("/registers", registerHandler::findAllRegisters)
                    .POST("/registers", registerHandler::registerMany)
                    .PUT("/updateSourceId", registerHandler::updateSourceIdByNumber)
                    .POST("/genKey", accept(APPLICATION_JSON), uniqueGenKeyHandler::generateUniqueKey)
                    .GET("/genKey", accept(APPLICATION_JSON), uniqueGenKeyHandler::findAllUniqueKeys)
                    .GET("/genKey/{" + PATH_SOURCE_ID + "}", uniqueGenKeyHandler::findByUniqueKey)
                    .GET("/number/{" + PATH_NUMBER + "}", registerHandler::findRegisterByNumber)
                    .GET("/dob/{" + PATH_DOB + "}", registerHandler::findRegisterByDateOfBirth)
                    .GET("/sourceId/{" + PATH_SOURCE_ID + "}", registerHandler::findRegisterBySourceId)
                    .GET("/sourceId/{" + PATH_SOURCE_ID + "}/type/{" + PATH_TYPE + "}", registerHandler::findBySourceIdAndWebscolaireType);
            }).build();

        return route().path("/", () -> wsRoutes).add(homeRoute).build();
    }
}
