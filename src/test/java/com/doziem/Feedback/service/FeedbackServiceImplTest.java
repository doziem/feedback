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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FeedbackServiceImplTest {

    @Mock
    private FeedbackRepository repository;

    @InjectMocks
    private FeedbackServiceImpl feedbackService;

    private final LocalDateTime now = LocalDateTime.now();
    private final String testUserId = "user-123";

    // Test data builder methods using direct builder approach
    private FeedbackRequest createRequest(Integer rating, String message, String userId) {
        return new FeedbackRequest(rating, message, userId);
    }

    private Feedback createFeedback(UUID id, Integer rating, String message, String userId, LocalDateTime createdAt) {
        return Feedback.builder()
                .id(id)
                .rating(rating)
                .message(message)
                .userId(userId)
                .createdAt(createdAt)
                .build();
    }

    @Test
    void submitFeedback_ValidRequest_ReturnsResponseWithUUID() {
        // Arrange
        FeedbackRequest request = createRequest(5, "Great service!", testUserId);
        Feedback savedFeedback = createFeedback(UUID.randomUUID(), 5, "Great service!", testUserId, now);

        when(repository.save(any(Feedback.class))).thenReturn(savedFeedback);

        // Act
        FeedbackResponse response = feedbackService.submitFeedback(request);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getId());
        assertEquals(5, response.getRating());
        assertEquals("Great service!", response.getMessage());
        assertEquals(testUserId, response.getUserId());
        verify(repository).save(any(Feedback.class));
    }

    @Test
    void submitFeedback_NullUserId_ThrowsException() {
        FeedbackRequest nullRequest = createRequest(3, "Average", null);
        FeedbackRequest emptyRequest = createRequest(3, "Average", "");

        assertAll(
                () -> assertThrows(InvalidFeedbackException.class,
                        () -> feedbackService.submitFeedback(nullRequest)),
                () -> assertThrows(InvalidFeedbackException.class,
                        () -> feedbackService.submitFeedback(emptyRequest))
        );
    }

    @Test
    void submitFeedback_InvalidRating_ThrowsException() {
        FeedbackRequest lowRating = createRequest(0, "Bad", testUserId);
        FeedbackRequest highRating = createRequest(6, "Great", testUserId);
        FeedbackRequest nullRating = createRequest(null, "Okay", testUserId);

        assertAll(
                () -> assertThrows(InvalidFeedbackException.class,
                        () -> feedbackService.submitFeedback(lowRating)),
                () -> assertThrows(InvalidFeedbackException.class,
                        () -> feedbackService.submitFeedback(highRating)),
                () -> assertThrows(InvalidFeedbackException.class,
                        () -> feedbackService.submitFeedback(nullRating))
        );
    }

    @Test
    void getAllFeedback_ReturnsOrderedResponses() {
        // Arrange
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        Feedback older = createFeedback(id1, 4, "Good", testUserId, now.minusDays(1));
        Feedback newer = createFeedback(id2, 5, "Great", "user-456", now);

        when(repository.findAllByOrderByCreatedAtDesc()).thenReturn(List.of(newer, older));

        // Act
        List<FeedbackResponse> responses = feedbackService.getAllFeedback();

        // Assert
        assertEquals(2, responses.size());
        assertEquals(id2, responses.get(0).getId()); // Newest first
        assertEquals(now, responses.get(0).getCreatedAt());
        assertEquals(id1, responses.get(1).getId());
        assertEquals(now.minusDays(1), responses.get(1).getCreatedAt());
        verify(repository).findAllByOrderByCreatedAtDesc();
    }

    @Test
    void getFilteredFeedbacks_ByRating_ReturnsFilteredList() {
        // Arrange
        UUID feedbackId = UUID.randomUUID();
        Integer targetRating = 4;

        Feedback matchingFeedback = createFeedback(feedbackId, targetRating, "Filtered content", testUserId, now);

        when(repository.findByRatingOrCreatedAt(targetRating, null))
                .thenReturn(List.of(matchingFeedback));

        // Act
        List<FeedbackResponse> result = feedbackService.getFeedbackByRatingOrDate(targetRating, null);

        // Assert
        assertEquals(1, result.size());
        assertEquals(targetRating, result.get(0).getRating());
        assertEquals(feedbackId, result.get(0).getId());
        verify(repository).findByRatingOrCreatedAt(targetRating, null);
    }

    @Test
    void mapToResponse_ValidFeedback_ReturnsCorrectResponse() {
        UUID feedbackId = UUID.randomUUID();
        Feedback feedback = createFeedback(feedbackId, 5, "Test content", testUserId, now);
        FeedbackResponse response = feedbackService.mapToResponse(feedback);

        assertEquals(feedbackId, response.getId());
        assertEquals(testUserId, response.getUserId());
        assertEquals(5, response.getRating());
        assertEquals("Test content", response.getMessage());
        assertEquals(now, response.getCreatedAt());
    }

    @Test
    void mapToResponse_NullFeedback_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> feedbackService.mapToResponse(null));

        assertEquals("Feedback cannot be null", exception.getMessage());
    }

}
