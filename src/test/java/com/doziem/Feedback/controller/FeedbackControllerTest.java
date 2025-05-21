package com.doziem.Feedback.controller;

import com.doziem.Feedback.config.SecurityConfig;
import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
@Import({ControllerTestConfig.class, SecurityConfig.class})
@AutoConfigureMockMvc(addFilters = false)
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private FeedbackService feedbackService;

    @BeforeEach
    void setup(@Autowired ApplicationContext context) {
        // Modern dependency injection
        feedbackService = context.getBean(FeedbackService.class);
    }

    @Test
    void submitFeedback_ValidRequest_Returns201() throws Exception {
        UUID testId = UUID.randomUUID();
        when(feedbackService.submitFeedback(any()))
                .thenReturn(new FeedbackResponse(testId, "user123", "Great", 5, null));

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {"userId":"user123","message":"Great","rating":5}
                    """))
                .andExpectAll(
                        status().isCreated(),
                        jsonPath("$.id").value(testId.toString()),
                        jsonPath("$.rating").value(5)
                );
    }

    @Test
    void submitFeedback_NullRequest_Returns400() throws Exception {
        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("null"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Feedback request cannot be null"));
    }
    @Test
    void submitFeedback_InvalidRating_Returns400() throws Exception {
        FeedbackRequest request = new FeedbackRequest("user123", "Bad", 0);

        when(feedbackService.submitFeedback(any()))
                .thenThrow(new InvalidFeedbackException("Rating must be between 1 and 5"));

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating must be between 1 and 5"));
    }

    @Test
    void submitFeedback_NullUserId_ThrowsException() throws Exception {
        // Given
        FeedbackRequest request = new FeedbackRequest(null, "Test feedback", 3);

        when(feedbackService.submitFeedback(any(FeedbackRequest.class)))
                .thenThrow(new InvalidFeedbackException("User ID cannot be null"));

        // When/Then
        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User ID cannot be null"));
    }
}


