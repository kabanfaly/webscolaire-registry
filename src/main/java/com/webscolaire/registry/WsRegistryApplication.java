package com.webscolaire.registry;

import com.webscolaire.registry.config.WsRegisterProperties;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
@SpringBootApplication
public class WsRegistryApplication {

    @Inject
    private WsRegisterProperties properties;
    
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(properties.getTimeZone()));
    }

    public static void main(String[] args) {
        SpringApplication.run(WsRegistryApplication.class, args);
    }
}
