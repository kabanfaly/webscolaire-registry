package com.webscolaire.register.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;

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
}
