package com.webscolaire.registry.config;

import static com.webscolaire.registry.config.WsRegisterConstants.DEFAULT_TIME_ZONE;
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
    private String timeZone = DEFAULT_TIME_ZONE;
}
