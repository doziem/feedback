package com.doziem.Feedback.service;


import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.model.Feedback;
import com.doziem.Feedback.repository.FeedbackRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceTest {

    @Mock
    private FeedbackRepository repository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    @Test
    void submitFeedback_ValidRequest_ReturnsResponse() {
        // Arrange
        FeedbackRequest request = new FeedbackRequest("user123", "Great service", 5);
        Feedback savedFeedback = Feedback.builder()
                .id(UUID.randomUUID())
                .userId("user123")
                .message("Great service")
                .rating(5)
                .build();

        when(repository.save(any(Feedback.class))).thenReturn(savedFeedback);

        // Act
        FeedbackResponse response = feedbackService.submitFeedback(request);

        // Assert
        assertThat(response)
                .isNotNull()
                .extracting(
                        FeedbackResponse::getUserId,
                        FeedbackResponse::getMessage,
                        FeedbackResponse::getRating
                )
                .containsExactly("user123", "Great service", 5);
    }

    @Test
    void submitFeedback_NullRequest_ThrowsException() {
        assertThatThrownBy(() -> feedbackService.submitFeedback(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Feedback request cannot be null");
    }

    @Test
    void submitFeedback_InvalidRating_ThrowsException() {
        FeedbackRequest request = new FeedbackRequest("user123", "Bad", 0);

        assertThatThrownBy(() -> feedbackService.submitFeedback(request))
                .isInstanceOf(InvalidFeedbackException.class)
                .hasMessage("Rating must be between 1 and 5");
    }

    @Test
    void submitFeedback_NullUserId_ThrowsException() {
        FeedbackRequest request = new FeedbackRequest(null, "Test", 3);

        assertThatThrownBy(() -> feedbackService.submitFeedback(request))
                .isInstanceOf(InvalidFeedbackException.class)
                .hasMessage("User ID cannot be null");
    }

}
