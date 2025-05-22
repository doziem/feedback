package com.doziem.Feedback.controller;

import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.service.FeedbackService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @InjectMocks
    private FeedbackService feedbackService;

    private FeedbackResponse createResponse(UUID id, String userId, String message, int rating, LocalDateTime createdAt) {
        FeedbackResponse response = new FeedbackResponse();
        response.setId(id);
        response.setUserId(userId);
        response.setMessage(message);
        response.setRating(rating);
        response.setCreatedAt(createdAt);
        return response;
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAllFeedback_AsAdmin_ShouldReturnFeedbackList() throws Exception {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        UUID id1 = UUID.randomUUID();
        UUID id2 = UUID.randomUUID();

        List<FeedbackResponse> mockResponses = List.of(
                createResponse(id2, "user2", "Good", 4, now),
                createResponse(id1, "user1", "Excellent!", 5, now.minusHours(1))
        );

        when(feedbackService.getAllFeedback()).thenReturn(mockResponses);

        // Act & Assert
        mockMvc.perform(get("/api/feedback")
                        .with(csrf())
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(id2.toString())))
                .andExpect(jsonPath("$[1].id", is(id1.toString())));
    }

    @Test
    public void getAllFeedback_WhenNoFeedback_ShouldReturnEmptyList() throws Exception {
        // Arrange
        when(feedbackService.getAllFeedback()).thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/api/feedback") // Replace with your actual endpoint
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(feedbackService, times(1)).getAllFeedback();
    }
}
