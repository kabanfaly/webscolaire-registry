package com.webscolaire.registry.service.impl;

import com.webscolaire.registry.domain.UniqueGenKey;
import com.webscolaire.registry.repository.UniqueGenKeyRepository;
import com.webscolaire.registry.service.UniqueGenKeyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.test.StepVerifier;

import java.util.UUID;

import static com.webscolaire.registry.TestUtils.MONGO_IMAGE;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Testcontainers
class UniqueGenKeyServiceImplTest {

    @Container
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));

    @Autowired
    private UniqueGenKeyService uniqueGenKeyService;

    @Autowired
    private UniqueGenKeyRepository uniqueKeyRepository;

    @BeforeEach
    public void setUp() {
        uniqueKeyRepository.deleteAll().block();
    }

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("MONGO_URI", mongoDBContainer::getReplicaSetUrl);
    }

    @DisplayName("Should generate and save uniqueKey")
    @Test
    void generateUniqueKey() {
        StepVerifier.create(uniqueGenKeyService.generateUniqueKey())
                .assertNext(uniqueGenKey -> assertNotNull(uniqueGenKey.getUniqueKey()))
                .verifyComplete();
    }

    @DisplayName("Should find uniqueKey")
    @Test
    void findByUniqueKeyOK() {
        var uniqueKey = UUID.randomUUID().toString().replace("-", "");
        uniqueKeyRepository.save(new UniqueGenKey().setUniqueKey(uniqueKey)).subscribe();
        StepVerifier.create(uniqueGenKeyService.findByUniqueKey(uniqueKey))
                .assertNext(uniqueGenKey -> assertEquals(uniqueKey, uniqueGenKey.getUniqueKey()))
                .verifyComplete();
    }

    @DisplayName("Should not find uniqueKey")
    @Test
    void findByUniqueKeyKO() {
        StepVerifier.create(uniqueGenKeyService.findByUniqueKey("AAAAAA"))
                .expectNextCount(0)
                .verifyComplete();
    }
}
