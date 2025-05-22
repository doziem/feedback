package com.doziem.Feedback.repository;

import com.doziem.Feedback.model.Feedback;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestRepositoryConfig.class)
public class FeedbackRepositoryTest {
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private FeedbackRepository repository;

    private final UUID testId = UUID.randomUUID();
    private final LocalDate today = LocalDate.now();
    private final LocalDate yesterday = today.minusDays(1);

    @BeforeEach
    void setUp() {
        repository.deleteAllInBatch();
    }

    @Test
    void findAllByOrderByCreatedAtDesc_ReturnsNewestFirst() {

        // Create test data with controlled timestamps
        Feedback older = saveFeedback("user1", "Old", 3,
                LocalDateTime.now().minusHours(2));
        Feedback newer = saveFeedback("user2", "New", 5,
                LocalDateTime.now());

        // Act
        List<Feedback> results = repository.findAllByOrderByCreatedAtDesc();

        // Assert
        assertThat(results)
                .hasSize(2)
                .extracting(Feedback::getMessage)
                .containsExactly("New", "Old"); // Case-sensitive match
    }

    private Feedback saveFeedback(String userId, String message, int rating, LocalDateTime createdAt) {
        Feedback feedback = Feedback.builder()
                .userId(userId)
                .message(message)
                .rating(rating)
                .createdAt(createdAt)
                .build();
        return repository.saveAndFlush(feedback);
    }
}

