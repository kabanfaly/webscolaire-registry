package com.webscolaire.register.service.impl;

import com.webscolaire.register.domain.WsRegister;
import com.webscolaire.register.domain.dto.WsRegisterParam;
import com.webscolaire.register.exception.WsRegisterError;
import com.webscolaire.register.exception.WsRegisterException;
import com.webscolaire.register.service.WsRegisterService;
import javax.inject.Inject;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 *
 * @since 13 avr. 2019
 * @author N'faly KABA
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class WsRegisterServiceImplTest {

    public static final String DOB = "20121999";
    public static final String GENERATED_NUMBER = "EE-20121999-10001";

    @Inject
    private WsRegisterService registerService;

    public WsRegisterServiceImplTest() {
    }

    @Test
    public void shouldThrowException_withWS_003_when_emptyOrNull_type() {
        try {
            registerService.registerOne(new WsRegisterParam("", null, "DOB", "", "", ""));
            fail("WsRegisterException(WS_003) expected");
        } catch (WsRegisterException ex) {
            assertEquals(WsRegisterError.WS_003, ex.getWsError());
        }
    }

    @Test
    public void shouldThrowException_withWS_001_when_invalid_type() {
        try {
            registerService.registerOne(new WsRegisterParam("", "INVALID_TYPE", DOB, "", "", ""));
            fail("WsRegisterException(WS_001) expected");
        } catch (WsRegisterException ex) {
            assertEquals(WsRegisterError.WS_001, ex.getWsError());
        }
    }

    @Test
    public void shouldThrowException_withWS_004_when_emptyOrNull_dateOfBirth() {
        try {
            registerService.registerOne(new WsRegisterParam("", "TYPE", "", "", "", ""));
            fail("WsRegisterException(WS_004) expected");
        } catch (WsRegisterException ex) {
            assertEquals(WsRegisterError.WS_004, ex.getWsError());
        }
    }

    @Test
    public void shouldThrowException_withWS_002_when_invalid_dateOfBirth() {
        try {
            registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", "40802222", "", "", ""));
            fail("WsRegisterException(WS_002) expected");
        } catch (WsRegisterException ex) {
            assertEquals(WsRegisterError.WS_002, ex.getWsError());
        }
    }

    @Test
    public void registerTestOK() {
        try {
            Mono<WsRegister> registerMono = registerService.registerOne(new WsRegisterParam("", "ELEVE_ETUDIANT", DOB, "", "", ""));
            StepVerifier.create(registerMono).expectComplete();
            WsRegister register = registerMono.block();

            assertEquals(GENERATED_NUMBER, register.getNumeroGenere());
            assertEquals(DOB, register.getDateNaissance());
        } catch (WsRegisterException ex) {
            fail("No exception expected");
        }
    }

}
