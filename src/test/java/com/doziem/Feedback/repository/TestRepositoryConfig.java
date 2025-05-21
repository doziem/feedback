package com.doziem.Feedback.repository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@TestConfiguration
public class TestRepositoryConfig {
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", FeedbackRepositoryTest.postgres::getJdbcUrl);
        registry.add("spring.datasource.username", FeedbackRepositoryTest.postgres::getUsername);
        registry.add("spring.datasource.password", FeedbackRepositoryTest.postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }
}
