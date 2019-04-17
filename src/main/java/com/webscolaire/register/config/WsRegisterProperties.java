package com.webscolaire.register.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@NoArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "webscolaire.generator")
public class WsRegisterProperties {

    private String username;
    private String password;
}
