package com.webscolaire.registry.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.server.SecurityWebFilterChain;

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
}
