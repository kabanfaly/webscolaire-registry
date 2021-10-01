package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.config.WsRegisterProperties;
import com.webscolaire.registry.domain.SexType;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.domain.WsRegister;
import com.webscolaire.registry.domain.dto.WsRegisterDto;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.repository.WsRegisterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.UUID;

import static com.webscolaire.registry.TestUtils.MONGO_IMAGE;
import static com.webscolaire.registry.domain.WebscolaireType.ELEVE_ETUDIANT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

/**
 * @author KABA N'faly
 * @since 15/12/2019 00:32
 */
@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WsRegisterServiceIT {

    @LocalServerPort
    private int port;

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

    private static final String SOURCE_ID = UUID.randomUUID().toString();
    private static final LocalDate DOB = LocalDate.of(1999, 10, 11);

    private WebTestClient webTestClient;

    @Autowired
    private WsRegisterProperties properties;

    @Autowired
    private WsRegisterRepository registerRepository;

    @Autowired
    private UniqueGenKeyRepository uniqueGenKeyRepository;

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(basicAuthentication(properties.username, properties.password))
                .defaultHeader("WS-TOKEN", "token")
                .build();
        registerRepository.deleteAll().block();
        uniqueGenKeyRepository.deleteAll().block();
        uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(SOURCE_ID)).block();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
    }

    public WsRegisterDto createWsRegisterDto() {
        return new WsRegisterDto().setSourceId(SOURCE_ID)
                .setWebscolaireType(ELEVE_ETUDIANT).setDateOfBirth(DOB)
                .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H);
    }

    @DisplayName("Should register with a 200 OK")
    @Test
    public void saveRegister() {
        var register = createWsRegisterDto();
        webTestClient.post()
                .uri("/register")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(register), WsRegisterDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.generatedNumber").isNotEmpty()
                .jsonPath("$.webscolaireType").isEqualTo(register.getWebscolaireType().name())
                .jsonPath("$.firstname").isEqualTo(register.getFirstname())
                .jsonPath("$.lastname").isEqualTo(register.getLastname())
                .jsonPath("$.sex").isEqualTo(register.getSex().name())
                .jsonPath("$.dateOfBirth").isEqualTo(register.getDateOfBirth().toString());
    }

    @DisplayName("Should return a bad request on saveRegister")
    @Test
    public void saveRegister_badRequest() {
        var register = createWsRegisterDto();
        register.setSourceId(null);
        webTestClient.post()
            .uri("/register")
            .contentType(APPLICATION_JSON)
            .accept(APPLICATION_JSON)
            .body(Mono.just(register), WsRegisterDto.class)
            .exchange()
            .expectStatus().isBadRequest();
    }

    @DisplayName("Should find by number with 200 OK")
    @Test
    public void findRegisterByNumber_OK() {
        String number = "AAAABBBB";
        registerRepository.save(new WsRegister()
                .setSourceId(SOURCE_ID)
                .setWebscolaireType(ELEVE_ETUDIANT)
                .setGeneratedNumber(number)).subscribe();

        webTestClient.get()
                .uri("/number/" + number)
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(WsRegister.class)
                .value(wsRegister -> {
                    assertEquals(number, wsRegister.getGeneratedNumber());
                    assertEquals(SOURCE_ID, wsRegister.getSourceId());
                });
    }

    @DisplayName("Should find by number with 404 NOT_FOUND")
    @Test
    public void findRegisterByNumber_notFound() {

        webTestClient.get()
                .uri("/number/AAABBB")
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }
}
