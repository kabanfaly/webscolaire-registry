package com.webscolaire.register;

import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.repository.WsRegisterRepository;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @since 11 avr. 2019
 * @author KABA N'faly
 */
@SpringBootApplication
public class WsRegisterApplication {

    @PostConstruct
    void started() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    public static void main(String[] args) {
        SpringApplication.run(WsRegisterApplication.class, args);
    }
}
