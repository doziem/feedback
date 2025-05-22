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

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
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

    @Test
    public void getAllFeedback_ShouldReturnAllFeedbackOrderedByCreatedAtDesc() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        Feedback feedback1 = new Feedback();
        feedback1.setId(UUID.randomUUID());
        feedback1.setUserId("user1");
        feedback1.setMessage("Great service!");
        feedback1.setRating(5);
        feedback1.setCreatedAt(now.minusDays(1));

        Feedback feedback2 = new Feedback();
        feedback2.setId(UUID.randomUUID());
        feedback2.setUserId("user2");
        feedback2.setMessage("Could be better");
        feedback2.setRating(3);
        feedback2.setCreatedAt(now);

        List<Feedback> mockFeedbackList = List.of(feedback2, feedback1);

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(mockFeedbackList);

        // Act
        List<FeedbackResponse> result = feedbackService.getAllFeedback();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(feedback2.getId(), result.get(0).getId());
        assertEquals(feedback1.getId(), result.get(1).getId());

        verify(repository, times(1)).findAllByOrderByCreatedAtDesc();
    }

    @Test
    public void getAllFeedback_WhenNoFeedbackExists_ShouldReturnEmptyList() {
        // Arrange
        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of());

        // Act
        List<FeedbackResponse> result = feedbackService.getAllFeedback();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(repository, times(1)).findAllByOrderByCreatedAtDesc();
    }
}
