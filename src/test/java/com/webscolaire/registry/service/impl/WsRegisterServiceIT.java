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
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.webscolaire.registry.domain.WebscolaireType.ELEVE_ETUDIANT;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * @author KABA N'faly
 * @since 15/12/2019 00:32
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class WsRegisterServiceIT {

    protected static final MongoDBContainer mongoDBContainer;

    static {
        mongoDBContainer = new MongoDBContainer(DockerImageName.parse("mongo:4.0.10"))
                .withReuse(true)
                .withLabel("reuse.UUID", "fb36b8c0-e03f-4d30-ad3a-38f16a8");
        mongoDBContainer.start();
    }

    @LocalServerPort
    private int port;

    private static final String SOURCE_ID = UUID.randomUUID().toString();
    private static final String DOB = "20121999";

    private WebTestClient webTestClient;

    @Autowired
    private WsRegisterProperties properties;

    @Autowired
    private WsRegisterRepository registerRepository;

    @Autowired
    private UniqueGenKeyRepository uniqueGenKeyRepository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
    }

    @BeforeEach
    public void setUp() {
        webTestClient = WebTestClient.bindToServer()
                .baseUrl("http://localhost:" + port)
                .filter(basicAuthentication(properties.getUsername(), properties.getPassword()))
                .defaultHeader("WS-TOKEN", "token")
                .build();
        registerRepository.deleteAll().block();
        uniqueGenKeyRepository.deleteAll().block();
        uniqueGenKeyRepository.save(new UniqueGenKey().setUniqueKey(SOURCE_ID)).block();
    }
    
    @AfterAll
    public static void tearDown() {
        mongoDBContainer.stop();
    }

    public WsRegisterDto createWsRegisterParam() {
        return new WsRegisterDto().setSourceId(SOURCE_ID)
                .setWebscolaireType(ELEVE_ETUDIANT.name()).setDateOfBirth(DOB)
                .setFirstname("N'faly").setLastname("KABA").setSex(SexType.H.name());
    }

    @DisplayName("Should register with a 200 OK")
    @Test
    public void testRegisterOneWith200() {
        webTestClient.post()
                .uri("/register")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .body(Mono.just(createWsRegisterParam()), WsRegisterDto.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.generatedNumber").isNotEmpty()
                .jsonPath("$.webscolaireType").isNotEmpty()
                .jsonPath("$.webscolaireType").isEqualTo(ELEVE_ETUDIANT.name());
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
