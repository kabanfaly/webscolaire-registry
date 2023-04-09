package com.webscolaire.registry;

import java.util.TimeZone;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;

/**
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
@SpringBootApplication
@EnableWebFluxSecurity
public class WsRegistryApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
      SpringApplication application = new SpringApplication(WsRegistryApplication.class);
      application.setWebApplicationType(WebApplicationType.REACTIVE);
      application.run(args);
    }
}
