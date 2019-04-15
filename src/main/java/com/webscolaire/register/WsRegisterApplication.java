package com.webscolaire.register;

import com.webscolaire.register.domain.WebscolaireType;
import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.repository.WsRegisterRepository;
import java.util.TimeZone;
import javax.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

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

    @Bean
    public CommandLineRunner run(WsRegisterRepository registerRepository) {
        return args -> {

            registerRepository.deleteAll()
                    .thenMany(
                            Flux.just("20121984", "20121985")
                                    .map(d -> new WsRegister()
                                    .setDateOfBirth(d).setWebscolaireType(WebscolaireType.ELEVE_ETUDIANT)
                                    .setSerialNumber(1000L).setGeneratedNumber(d + "-1000"))
//                                    .flatMap(registerRepository::save)
                                    .thenMany(registerRepository.findAll()))
                    .subscribe(System.out::println);
        };
    }

    public static void main(String[] args) {
        SpringApplication.run(WsRegisterApplication.class, args);
    }
}
