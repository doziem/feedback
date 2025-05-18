package com.doziem.Feedback.controller;
import com.doziem.Feedback.dto.FeedbackRequest;
import com.doziem.Feedback.dto.FeedbackResponse;
import com.doziem.Feedback.exception.InvalidFeedbackException;
import com.doziem.Feedback.service.FeedbackService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FeedbackController.class)
@AutoConfigureMockMvc(addFilters = false)
public class FeedbackControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Mock
    private FeedbackService feedbackService;

    @InjectMocks
    private FeedbackController feedbackController;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    private final UUID testId = UUID.randomUUID();
    private final LocalDateTime testTime = LocalDateTime.now();
    private final FeedbackResponse testResponse = FeedbackResponse.builder()
            .id(testId)
            .userId("user123")
            .message("Great service")
            .rating(5)
            .createdAt(testTime)
            .build();

    @Test
    void submitFeedback_ValidInput_ReturnsCreated() throws Exception {
        FeedbackRequest request = new FeedbackRequest(5, "Great service", "user123");

        when(feedbackService.submitFeedback(any(FeedbackRequest.class)))
                .thenReturn(testResponse);

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(testId.toString()))
                .andExpect(jsonPath("$.userId").value("user123"))
                .andExpect(jsonPath("$.rating").value(5));

        verify(feedbackService).submitFeedback(any(FeedbackRequest.class));
    }

    @Test
    void submitFeedback_InvalidRating_ReturnsBadRequest() throws Exception {
        FeedbackRequest invalidRequest = new FeedbackRequest(6, "Test", "user123");

        when(feedbackService.submitFeedback(any()))
                .thenThrow(new InvalidFeedbackException("Invalid rating"));

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitFeedback_MissingFields_ReturnsBadRequest() throws Exception {
        String invalidJson = """
            {
                "rating": 0,
                "message": "",
                "userId": ""
            }
            """;

        mockMvc.perform(post("/api/feedback")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());
    }
}
