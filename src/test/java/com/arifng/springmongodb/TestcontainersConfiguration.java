package com.arifng.springmongodb;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.DynamicPropertyRegistrar;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestcontainersConfiguration {
    @Bean
    MongoDBContainer mongoDBContainer() {
        return new MongoDBContainer(DockerImageName.parse("mongo:8")); // Use the latest MongoDB image, 8 is currently the latest LTS version
    }

    @Bean
    DynamicPropertyRegistrar dynamicPropertyRegistrar(MongoDBContainer mongoDBContainer) {
        return registry -> {
            registry.add("spring.data.mongodb.uri", mongoDBContainer::getConnectionString);
            registry.add("spring.data.mongodb.database", () -> "testdb");
        };
    }
}
