package com.webscolaire.registry.handler;

import com.webscolaire.registry.config.RegisterProperties;
import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
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

import static com.webscolaire.registry.TestUtils.MONGO_IMAGE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.client.ExchangeFilterFunctions.basicAuthentication;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class UniqueGenKeyHandlerTest {

  @LocalServerPort
  private int port;

  @Container
  private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

  private WebTestClient webTestClient;

  @Autowired
  private RegisterProperties properties;

  @Autowired
  private UniqueGenKeyRepository uniqueGenKeyRepository;


  @BeforeEach
  public void setUp() {
    webTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .filter(basicAuthentication(properties.username, properties.password))
        .defaultHeader("WS-TOKEN", "token")
        .build();
    uniqueGenKeyRepository.deleteAll().block();
  }

  @DynamicPropertySource
  static void configure(DynamicPropertyRegistry registry) {
    registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
  }

  @DisplayName("Should generate a UniqueGenKey")
  @Test
  public void generateUniqueKey() {
    webTestClient.post()
        .uri("/genKey")
        .contentType(APPLICATION_JSON)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isNotEmpty();
  }

  @DisplayName("Should find all UniqueGenKeys")
  @Test
  public void findAllUniqueKeys() {
    var id = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
    webTestClient.get()
        .uri("/genKey")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.length()").isEqualTo(1)
        .jsonPath("$[0].id").isEqualTo(id);
  }

  @DisplayName("Should find a UniqueGenKey")
  @Test
  public void findByUniqueKey() {
    var id = uniqueGenKeyRepository.save(new UniqueGenKey()).block().getId();
    webTestClient.get()
        .uri("/genKey/" + id)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(id);
  }

  @DisplayName("Should find UniqueGenKey with 404 Not Found")
  @Test
  public void findByUniqueKey_notFound() {
    webTestClient.get()
        .uri("/genKey/not-found")
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

}
