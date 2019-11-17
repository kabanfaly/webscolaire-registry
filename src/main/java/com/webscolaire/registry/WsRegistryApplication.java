package com.webscolaire.registry;

import com.webscolaire.registry.config.WsRegisterProperties;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebFlux;

/**
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
@SpringBootApplication
@EnableSwagger2WebFlux
public class WsRegistryApplication {

    @Inject
    private WsRegisterProperties properties;
    
    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone(properties.getTimeZone()));
    }

    public static void main(String[] args) {
      SpringApplication application = new SpringApplication(WsRegistryApplication.class);
      application.setWebApplicationType(WebApplicationType.REACTIVE);
      application.run(args);
    }
}
